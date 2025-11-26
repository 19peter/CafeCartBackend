package com.peters.cafecart.features.OrderManagement.dto;

import java.math.BigDecimal;
import java.util.List;

import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;
import com.peters.cafecart.shared.enums.OrderTypeEnum;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    Long id;
    String orderNumber;
    OrderTypeEnum orderType;
    PaymentMethodEnum paymentMethod;
    OrderStatusEnum status;
    List<OrderItemDto> items;
    BigDecimal totalPrice;
    
}
