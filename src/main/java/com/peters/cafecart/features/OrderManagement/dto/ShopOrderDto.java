package com.peters.cafecart.features.OrderManagement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;
import com.peters.cafecart.shared.enums.OrderTypeEnum;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ShopOrderDto {
    Long id;
    String orderNumber;
    OrderTypeEnum orderType;
    PaymentMethodEnum paymentMethod;
    OrderStatusEnum status;
    List<OrderItemDto> items;
    BigDecimal totalPrice;
    LocalDateTime createdAt;

    Long customerId;
    String customerName;
    String phone;
    String address;

    Double latitude;
    Double longitude;
}
