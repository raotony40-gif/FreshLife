package com.freshlife.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVO {

    private Long cartId;

    private Long productId;

    private String productName;

    private String productImageUrl;

    private BigDecimal productPrice;

    private Integer quantity;

    private BigDecimal subtotal;
}
