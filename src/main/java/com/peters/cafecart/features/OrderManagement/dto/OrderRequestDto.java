package com.peters.cafecart.features.OrderManagement.dto;

import com.peters.cafecart.shared.enums.OrderTypeEnum;

import lombok.Getter;

@Getter
public class OrderRequestDto {
    private Long cartId;
    private Long vendorShopId;
    private String deliveryAddress;
    private OrderTypeEnum orderType;
}

