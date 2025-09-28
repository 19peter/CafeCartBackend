package com.peters.cafecart.features.CartManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartDto {
    private Long customerId;
    private Long productId;
    private Long shopId;
    private int quantity;
}
