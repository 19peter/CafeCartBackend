package com.peters.cafecart.features.CartManagement.dto.response;


import com.peters.cafecart.features.CartManagement.dto.CartSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.OrderSummaryDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartAndOrderSummaryDto {
    private CartSummaryDto cartSummary;
    private OrderSummaryDto orderSummary;
}
