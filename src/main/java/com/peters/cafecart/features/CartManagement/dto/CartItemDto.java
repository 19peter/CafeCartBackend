package com.peters.cafecart.features.CartManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {
    private Long id;
    private Long cartId;
    private Long productId;
    private Long productOptionId;
    private String productName;
    private String productImage;
    private int quantity;
    private double unitPrice;
    private boolean isStockTracked;
}
