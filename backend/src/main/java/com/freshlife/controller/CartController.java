package com.freshlife.controller;

import com.freshlife.common.Result;
import com.freshlife.dto.CartAddDTO;
import com.freshlife.dto.CartUpdateDTO;
import com.freshlife.service.CartService;
import com.freshlife.vo.CartItemVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public Result<Boolean> add(
            HttpServletRequest request,
            @Valid @RequestBody CartAddDTO addDTO) {
        return Result.success(cartService.add(request, addDTO));
    }

    @GetMapping("/list")
    public Result<List<CartItemVO>> list(HttpServletRequest request) {
        return Result.success(cartService.list(request));
    }

    @PutMapping("/update")
    public Result<Boolean> update(
            HttpServletRequest request,
            @Valid @RequestBody CartUpdateDTO updateDTO) {
        return Result.success(cartService.updateQuantity(request, updateDTO));
    }

    @DeleteMapping("/remove/{cartId}")
    public Result<Boolean> remove(
            HttpServletRequest request,
            @PathVariable Long cartId) {
        return Result.success(cartService.remove(request, cartId));
    }
}
