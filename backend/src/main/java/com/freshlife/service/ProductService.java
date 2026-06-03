package com.freshlife.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.freshlife.entity.Product;

public interface ProductService extends IService<Product> {

    Page<Product> listProducts(Long page, Long size, String priceSort);

    Product getProductDetail(Long id);

    Page<Product> searchProducts(String name, Long page, Long size, String priceSort);
}
