package com.peters.cafecart.features.OrderManagement.dto;

import java.time.LocalDateTime;

import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;
import com.peters.cafecart.shared.enums.OrderTypeEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    Long id;
    String orderNumber;
    String deliveryAddress;
    OrderTypeEnum orderType;
    OrderStatusEnum status;
    LocalDateTime createdAt;
    Integer itemCount;
}
