package com.peters.cafecart.features.CartManagement.dto;

import java.util.List;

import com.peters.cafecart.shared.enums.OrderTypeEnum;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSummaryDto {
    
    private List<CartItemDto> items;
    private double subTotal;
    private OrderTypeEnum orderType;
    private PaymentMethodEnum paymentMethod;
    private double deliveryFee;
    private double transactionFee;
    private double total;
}
