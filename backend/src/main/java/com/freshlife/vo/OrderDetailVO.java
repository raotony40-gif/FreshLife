package com.freshlife.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {

    private Long orderId;

    private String orderNo;

    private BigDecimal totalAmount;

    private BigDecimal deliveryFee;

    private BigDecimal payAmount;

    private String status;

    private LocalDateTime createTime;

    private List<Item> items;

    @Data
    public static class Item {

        private Long productId;

        private String productName;

        private String productImageUrl;

        private BigDecimal productPrice;

        private Integer quantity;

        private BigDecimal subtotal;
    }
}
