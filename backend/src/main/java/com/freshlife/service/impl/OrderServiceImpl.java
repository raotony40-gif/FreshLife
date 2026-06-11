package com.freshlife.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshlife.entity.Cart;
import com.freshlife.entity.OrderItem;
import com.freshlife.entity.Orders;
import com.freshlife.entity.Product;
import com.freshlife.exception.BusinessException;
import com.freshlife.mapper.CartMapper;
import com.freshlife.mapper.OrderItemMapper;
import com.freshlife.mapper.OrdersMapper;
import com.freshlife.mapper.ProductMapper;
import com.freshlife.service.OrderService;
import com.freshlife.utils.JwtUserUtils;
import com.freshlife.vo.OrderCreateVO;
import com.freshlife.vo.OrderDetailVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrderService {

    private static final int PRODUCT_STATUS_ON_SALE = 1;
    private static final String ORDER_STATUS_WAIT_PAY = "WAIT_PAY";
    private static final String ORDER_STATUS_PAID = "PAID";
    private static final String ORDER_STATUS_CANCELLED = "CANCELLED";
    private static final String ORDER_STATUS_FINISHED = "FINISHED";

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final OrderItemMapper orderItemMapper;
    private final JwtUserUtils jwtUserUtils;

    public OrderServiceImpl(
            CartMapper cartMapper,
            ProductMapper productMapper,
            OrderItemMapper orderItemMapper,
            JwtUserUtils jwtUserUtils) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
        this.orderItemMapper = orderItemMapper;
        this.jwtUserUtils = jwtUserUtils;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCreateVO create(HttpServletRequest request) {
        Long userId = jwtUserUtils.getCurrentUserId(request);
        List<Cart> carts = cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));
        if (carts.isEmpty()) {
            throw new BusinessException(400, "购物车不能为空");
        }

        List<Long> productIds = carts.stream()
                .map(Cart::getProductId)
                .distinct()
                .toList();
        Map<Long, Product> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        Map<Long, Integer> quantityMap = carts.stream()
                .collect(Collectors.groupingBy(Cart::getProductId, Collectors.summingInt(Cart::getQuantity)));

        validateProducts(productIds, productMap, quantityMap);

        BigDecimal totalAmount = carts.stream()
                .map(cart -> productMap.get(cart.getProductId()).getPrice()
                        .multiply(BigDecimal.valueOf(cart.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime now = LocalDateTime.now();
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setDeliveryFee(BigDecimal.ZERO);
        order.setPayAmount(totalAmount);
        order.setStatus(ORDER_STATUS_WAIT_PAY);
        order.setCreateTime(now);
        order.setUpdateTime(now);
        order.setDeleted(0);
        save(order);

        for (Cart cart : carts) {
            Product product = productMap.get(cart.getProductId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImageUrl(product.getImageUrl());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            orderItem.setDeleted(0);
            orderItemMapper.insert(orderItem);
        }

        for (Map.Entry<Long, Integer> entry : quantityMap.entrySet()) {
            int updated = productMapper.update(null, new LambdaUpdateWrapper<Product>()
                    .eq(Product::getId, entry.getKey())
                    .eq(Product::getStatus, PRODUCT_STATUS_ON_SALE)
                    .ge(Product::getStock, entry.getValue())
                    .setSql("stock = stock - " + entry.getValue()));
            if (updated <= 0) {
                throw new BusinessException(409, "商品库存不足");
            }
        }

        cartMapper.delete(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));

        return toOrderCreateVO(order);
    }

    @Override
    public List<OrderCreateVO> list(HttpServletRequest request) {
        Long userId = jwtUserUtils.getCurrentUserId(request);
        return list(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getUserId, userId)
                .orderByDesc(Orders::getCreateTime))
                .stream()
                .map(this::toOrderCreateVO)
                .toList();
    }

    @Override
    public OrderDetailVO detail(HttpServletRequest request, Long orderId) {
        Long userId = jwtUserUtils.getCurrentUserId(request);
        Orders order = getUserOrder(userId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId()));
        return toOrderDetailVO(order, items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancel(HttpServletRequest request, Long orderId) {
        Long userId = jwtUserUtils.getCurrentUserId(request);
        if (orderId == null || orderId <= 0) {
            throw new BusinessException(400, "订单ID不能为空");
        }

        int updated = baseMapper.update(null, new LambdaUpdateWrapper<Orders>()
                .eq(Orders::getId, orderId)
                .eq(Orders::getUserId, userId)
                .eq(Orders::getStatus, ORDER_STATUS_WAIT_PAY)
                .set(Orders::getStatus, ORDER_STATUS_CANCELLED)
                .set(Orders::getCancelTime, LocalDateTime.now()));
        if (updated <= 0) {
            throw new BusinessException(409, "订单不存在或当前状态不允许取消");
        }

        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            productMapper.update(null, new LambdaUpdateWrapper<Product>()
                    .eq(Product::getId, item.getProductId())
                    .setSql("stock = stock + " + item.getQuantity()));
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean pay(HttpServletRequest request, Long orderId) {
        Long userId = jwtUserUtils.getCurrentUserId(request);
        if (orderId == null || orderId <= 0) {
            throw new BusinessException(400, "订单ID不能为空");
        }

        int updated = baseMapper.update(null, new LambdaUpdateWrapper<Orders>()
                .eq(Orders::getId, orderId)
                .eq(Orders::getUserId, userId)
                .eq(Orders::getStatus, ORDER_STATUS_WAIT_PAY)
                .set(Orders::getStatus, ORDER_STATUS_PAID)
                .set(Orders::getPayTime, LocalDateTime.now())
                .set(Orders::getUpdateTime, LocalDateTime.now()));
        if (updated <= 0) {
            throw new BusinessException(409, "订单不存在或当前状态不允许支付");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean finish(HttpServletRequest request, Long orderId) {
        Long userId = jwtUserUtils.getCurrentUserId(request);

        if (orderId == null || orderId <= 0) {
            throw new BusinessException(400, "订单ID不能为空");
        }

        int updated = baseMapper.update(null, new LambdaUpdateWrapper<Orders>()
                .eq(Orders::getId, orderId)
                .eq(Orders::getUserId, userId)
                .eq(Orders::getStatus, ORDER_STATUS_PAID)
                .set(Orders::getStatus, ORDER_STATUS_FINISHED)
                .set(Orders::getFinishTime, LocalDateTime.now())
                .set(Orders::getUpdateTime, LocalDateTime.now()));

        if (updated <= 0) {
            throw new BusinessException(409, "订单不存在或当前状态不允许完成");
        }

        return true;
    }

    private void validateProducts(List<Long> productIds, Map<Long, Product> productMap, Map<Long, Integer> quantityMap) {
        for (Long productId : productIds) {
            Product product = productMap.get(productId);
            if (product == null) {
                throw new BusinessException(404, "商品不存在");
            }
            if (!Integer.valueOf(PRODUCT_STATUS_ON_SALE).equals(product.getStatus())) {
                throw new BusinessException(409, "商品已下架");
            }
            if (product.getStock() == null || product.getStock() < quantityMap.get(productId)) {
                throw new BusinessException(409, "商品库存不足");
            }
        }
    }

    private Orders getUserOrder(Long userId, Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException(400, "订单ID不能为空");
        }
        Orders order = getOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getId, orderId)
                .eq(Orders::getUserId, userId)
                .last("LIMIT 1"));
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        int random = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return "FL" + timestamp + random;
    }

    private OrderCreateVO toOrderCreateVO(Orders order) {
        OrderCreateVO vo = new OrderCreateVO();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setStatus(order.getStatus());
        vo.setCreateTime(order.getCreateTime());
        return vo;
    }

    private OrderDetailVO toOrderDetailVO(Orders order, List<OrderItem> items) {
        OrderDetailVO vo = new OrderDetailVO();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setDeliveryFee(order.getDeliveryFee());
        vo.setPayAmount(order.getPayAmount());
        vo.setStatus(order.getStatus());
        vo.setCreateTime(order.getCreateTime());
        vo.setItems(items.stream()
                .filter(Objects::nonNull)
                .map(this::toOrderDetailItem)
                .toList());
        return vo;
    }

    private OrderDetailVO.Item toOrderDetailItem(OrderItem orderItem) {
        OrderDetailVO.Item item = new OrderDetailVO.Item();
        item.setProductId(orderItem.getProductId());
        item.setProductName(orderItem.getProductName());
        item.setProductImageUrl(orderItem.getProductImageUrl());
        item.setProductPrice(orderItem.getProductPrice());
        item.setQuantity(orderItem.getQuantity());
        item.setSubtotal(orderItem.getSubtotal());
        return item;
    }
}
