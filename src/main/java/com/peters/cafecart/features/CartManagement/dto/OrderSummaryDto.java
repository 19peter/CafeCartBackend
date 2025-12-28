package com.peters.cafecart.features.CartManagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.peters.cafecart.features.CartManagement.dto.base.OrderTypeBase;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSummaryDto {
    
    private List<CartItemDto> items = new ArrayList<>();
    private double subTotal;
    private PaymentMethodEnum paymentMethod;
    private double transactionFee;
    private double total;
    private String shopName;
    private OrderTypeBase orderTypeBase;
}
