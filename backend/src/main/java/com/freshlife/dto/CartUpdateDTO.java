package com.freshlife.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartUpdateDTO {

    @NotNull(message = "cartId不能为空")
    private Long cartId;

    @NotNull(message = "quantity不能为空")
    @Min(value = 1, message = "quantity必须大于0")
    private Integer quantity;
}
