package com.freshlife.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshlife.entity.Product;
import com.freshlife.exception.BusinessException;
import com.freshlife.mapper.ProductMapper;
import com.freshlife.service.ProductService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private static final int PRODUCT_STATUS_ON_SALE = 1;
    private static final String PRODUCT_CACHE_KEY_PREFIX = "product:";
    private static final String PRODUCT_LIST_CACHE_KEY_PREFIX = "product:list:";
    private static final String EMPTY_PRODUCT_CACHE_VALUE = "EMPTY";
    private static final Duration PRODUCT_CACHE_TTL = Duration.ofMinutes(30);
    private static final Duration PRODUCT_LIST_CACHE_TTL = Duration.ofMinutes(10);
    private static final Duration EMPTY_PRODUCT_CACHE_TTL = Duration.ofMinutes(5);
    private static final long DEFAULT_PAGE = 1L;
    private static final long DEFAULT_SIZE = 10L;
    private static final long MAX_SIZE = 100L;
    private static final String SORT_ASC = "asc";
    private static final String SORT_DESC = "desc";

    private final RedisTemplate<String, Object> redisTemplate;

    public ProductServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Page<Product> listProducts(Long page, Long size, String priceSort) {
        long current = normalizePage(page);
        long pageSize = normalizeSize(size);
        String normalizedPriceSort = normalizePriceSort(priceSort);
        String cacheKey = PRODUCT_LIST_CACHE_KEY_PREFIX + current + ":" + pageSize + ":" + normalizedPriceSort;

        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
        if (cachedValue instanceof Page<?> cachedPage) {
            return (Page<Product>) cachedPage;
        }

        LambdaQueryWrapper<Product> queryWrapper = baseOnSaleQuery();
        applyPriceSort(queryWrapper, normalizedPriceSort);
        queryWrapper.orderByDesc(!StringUtils.hasText(normalizedPriceSort), Product::getCreateTime);
        Page<Product> productPage = page(new Page<>(current, pageSize), queryWrapper);
        redisTemplate.opsForValue().set(cacheKey, productPage, PRODUCT_LIST_CACHE_TTL);
        return productPage;
    }

    @Override
    public Product getProductDetail(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(400, "商品ID不能为空");
        }

        String cacheKey = PRODUCT_CACHE_KEY_PREFIX + id;
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
        if (EMPTY_PRODUCT_CACHE_VALUE.equals(cachedValue)) {
            throw new BusinessException(404, "商品不存在");
        }
        if (cachedValue instanceof Product product) {
            return product;
        }

        Product product = getOne(baseOnSaleQuery()
                .eq(Product::getId, id)
                .last("LIMIT 1"));
        if (product == null) {
            redisTemplate.opsForValue().set(cacheKey, EMPTY_PRODUCT_CACHE_VALUE, EMPTY_PRODUCT_CACHE_TTL);
            throw new BusinessException(404, "商品不存在");
        }
        redisTemplate.opsForValue().set(cacheKey, product, PRODUCT_CACHE_TTL);
        return product;
    }

    @Override
    public Page<Product> searchProducts(String name, Long page, Long size, String priceSort) {
        if (!StringUtils.hasText(name)) {
            throw new BusinessException(400, "搜索关键词不能为空");
        }

        LambdaQueryWrapper<Product> queryWrapper = baseOnSaleQuery()
                .like(Product::getName, name.trim());
        applyPriceSort(queryWrapper, priceSort);
        queryWrapper.orderByDesc(!StringUtils.hasText(priceSort), Product::getCreateTime);
        return page(buildPage(page, size), queryWrapper);
    }

    private LambdaQueryWrapper<Product> baseOnSaleQuery() {
        return new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, PRODUCT_STATUS_ON_SALE);
    }

    private Page<Product> buildPage(Long page, Long size) {
        return new Page<>(normalizePage(page), normalizeSize(size));
    }

    private void applyPriceSort(LambdaQueryWrapper<Product> queryWrapper, String priceSort) {
        if (!StringUtils.hasText(priceSort)) {
            return;
        }

        String sort = priceSort.trim().toLowerCase();
        if (SORT_ASC.equals(sort)) {
            queryWrapper.orderByAsc(Product::getPrice);
            return;
        }
        if (SORT_DESC.equals(sort)) {
            queryWrapper.orderByDesc(Product::getPrice);
            return;
        }
        throw new BusinessException(400, "价格排序参数只能是asc或desc");
    }

    private long normalizePage(Long page) {
        return page == null || page < 1 ? DEFAULT_PAGE : page;
    }

    private long normalizeSize(Long size) {
        return size == null || size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
    }

    private String normalizePriceSort(String priceSort) {
        return StringUtils.hasText(priceSort) ? priceSort.trim().toLowerCase() : "";
    }
}
