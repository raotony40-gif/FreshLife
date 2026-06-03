package com.freshlife.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.freshlife.dto.CartAddDTO;
import com.freshlife.dto.CartUpdateDTO;
import com.freshlife.entity.Cart;
import com.freshlife.vo.CartItemVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CartService extends IService<Cart> {

    Boolean add(HttpServletRequest request, CartAddDTO addDTO);

    List<CartItemVO> list(HttpServletRequest request);

    Boolean updateQuantity(HttpServletRequest request, CartUpdateDTO updateDTO);

    Boolean remove(HttpServletRequest request, Long cartId);
}
