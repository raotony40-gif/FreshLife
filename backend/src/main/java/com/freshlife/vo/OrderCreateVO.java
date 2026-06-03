package com.freshlife.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderCreateVO {

    private Long orderId;

    private String orderNo;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private String status;

    private LocalDateTime createTime;
}
