package com.freshlife.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.freshlife.entity.Orders;
import com.freshlife.vo.OrderCreateVO;
import com.freshlife.vo.OrderDetailVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService extends IService<Orders> {

    OrderCreateVO create(HttpServletRequest request);

    List<OrderCreateVO> list(HttpServletRequest request);

    OrderDetailVO detail(HttpServletRequest request, Long orderId);

    Boolean cancel(HttpServletRequest request, Long orderId);

    Boolean pay(HttpServletRequest request, Long orderId);

    Boolean finish(HttpServletRequest request, Long orderId);
}
