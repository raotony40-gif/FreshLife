package com.freshlife.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshlife.common.Result;
import com.freshlife.entity.Product;
import com.freshlife.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/list")
    public Result<Page<Product>> list(
            @RequestParam(required = false) Long page,
            @RequestParam(required = false) Long size,
            @RequestParam(required = false) String priceSort) {
        return Result.success(productService.listProducts(page, size, priceSort));
    }

    @GetMapping("/detail/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }

    @GetMapping("/search")
    public Result<Page<Product>> search(
            @RequestParam String name,
            @RequestParam(required = false) Long page,
            @RequestParam(required = false) Long size,
            @RequestParam(required = false) String priceSort) {
        return Result.success(productService.searchProducts(name, page, size, priceSort));
    }
}
