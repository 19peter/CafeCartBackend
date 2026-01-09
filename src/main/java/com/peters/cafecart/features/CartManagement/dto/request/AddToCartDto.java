package com.peters.cafecart.features.CartManagement.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartDto {
    private Long productId;
    private Long shopId;
    private Integer quantity;
}
