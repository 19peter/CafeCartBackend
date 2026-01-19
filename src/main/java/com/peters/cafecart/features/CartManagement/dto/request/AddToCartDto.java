package com.peters.cafecart.features.CartManagement.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartDto {
    private Long productOptionId;
    private Long shopId;
    private Integer quantity;
}
