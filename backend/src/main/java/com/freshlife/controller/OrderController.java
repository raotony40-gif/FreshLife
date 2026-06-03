package com.freshlife.controller;

import com.freshlife.common.Result;
import com.freshlife.service.OrderService;
import com.freshlife.vo.OrderCreateVO;
import com.freshlife.vo.OrderDetailVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public Result<OrderCreateVO> create(HttpServletRequest request) {
        return Result.success(orderService.create(request));
    }

    @GetMapping("/list")
    public Result<List<OrderCreateVO>> list(HttpServletRequest request) {
        return Result.success(orderService.list(request));
    }

    @GetMapping("/detail/{orderId}")
    public Result<OrderDetailVO> detail(
            HttpServletRequest request,
            @PathVariable Long orderId) {
        return Result.success(orderService.detail(request, orderId));
    }

    @PutMapping("/cancel/{orderId}")
    public Result<Boolean> cancel(
            HttpServletRequest request,
            @PathVariable Long orderId) {
        return Result.success(orderService.cancel(request, orderId));
    }
}
