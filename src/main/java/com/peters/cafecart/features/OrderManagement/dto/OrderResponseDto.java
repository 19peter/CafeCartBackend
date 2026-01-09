package com.peters.cafecart.features.OrderManagement.dto;

import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private OrderStatusEnum status;
}