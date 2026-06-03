package com.freshlife.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshlife.dto.CartAddDTO;
import com.freshlife.dto.CartUpdateDTO;
import com.freshlife.entity.Cart;
import com.freshlife.entity.Product;
import com.freshlife.exception.BusinessException;
import com.freshlife.mapper.CartMapper;
import com.freshlife.mapper.ProductMapper;
import com.freshlife.service.CartService;
import com.freshlife.utils.JwtUserUtils;
import com.freshlife.vo.CartItemVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private static final int PRODUCT_STATUS_ON_SALE = 1;

    private final ProductMapper productMapper;
    private final JwtUserUtils jwtUserUtils;

    public CartServiceImpl(ProductMapper productMapper, JwtUserUtils jwtUserUtils) {
        this.productMapper = productMapper;
        this.jwtUserUtils = jwtUserUtils;
    }

    @Override
    public Boolean add(HttpServletRequest request, CartAddDTO addDTO) {
        Long userId = jwtUserUtils.getCurrentUserId(request);
        Product product = getAvailableProduct(addDTO.getProductId());

        Cart cart = getOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, product.getId())
                .last("LIMIT 1"));

        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(product.getId());
            cart.setQuantity(addDTO.getQuantity());
            cart.setDeleted(0);
            return save(cart);
        }

        cart.setQuantity(cart.getQuantity() + addDTO.getQuantity());
        return updateById(cart);
    }

    @Override
    public List<CartItemVO> list(HttpServletRequest request) {
        Long userId = jwtUserUtils.getCurrentUserId(request);
        List<Cart> carts = list(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getUpdateTime));

        if (carts.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> productIds = carts.stream()
                .map(Cart::getProductId)
                .distinct()
                .toList();
        Map<Long, Product> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return carts.stream()
                .map(cart -> toCartItemVO(cart, productMap.get(cart.getProductId())))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Boolean updateQuantity(HttpServletRequest request, CartUpdateDTO updateDTO) {
        Long userId = jwtUserUtils.getCurrentUserId(request);
        Cart cart = getUserCart(userId, updateDTO.getCartId());
        cart.setQuantity(updateDTO.getQuantity());
        return updateById(cart);
    }

    @Override
    public Boolean remove(HttpServletRequest request, Long cartId) {
        Long userId = jwtUserUtils.getCurrentUserId(request);
        Cart cart = getUserCart(userId, cartId);
        return removeById(cart.getId());
    }

    private Product getAvailableProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (!Integer.valueOf(PRODUCT_STATUS_ON_SALE).equals(product.getStatus())) {
            throw new BusinessException(409, "商品已下架");
        }
        return product;
    }

    private Cart getUserCart(Long userId, Long cartId) {
        if (cartId == null || cartId <= 0) {
            throw new BusinessException(400, "cartId不能为空");
        }

        Cart cart = getOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getId, cartId)
                .eq(Cart::getUserId, userId)
                .last("LIMIT 1"));
        if (cart == null) {
            throw new BusinessException(404, "购物车项不存在");
        }
        return cart;
    }

    private CartItemVO toCartItemVO(Cart cart, Product product) {
        if (product == null) {
            return null;
        }

        BigDecimal price = product.getPrice();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cart.getQuantity()));

        CartItemVO cartItemVO = new CartItemVO();
        cartItemVO.setCartId(cart.getId());
        cartItemVO.setProductId(product.getId());
        cartItemVO.setProductName(product.getName());
        cartItemVO.setProductImageUrl(product.getImageUrl());
        cartItemVO.setProductPrice(price);
        cartItemVO.setQuantity(cart.getQuantity());
        cartItemVO.setSubtotal(subtotal);
        return cartItemVO;
    }
}
