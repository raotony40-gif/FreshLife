package com.freshlife.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartAddDTO {

    @NotNull(message = "productId不能为空")
    private Long productId;

    @NotNull(message = "quantity不能为空")
    @Min(value = 1, message = "quantity必须大于0")
    private Integer quantity;
}
