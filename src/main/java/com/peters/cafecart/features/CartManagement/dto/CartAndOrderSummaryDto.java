package com.peters.cafecart.features.CartManagement.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartAndOrderSummaryDto {
    private CartSummaryDto cartSummary;
    private OrderSummaryDto orderSummary;
}
