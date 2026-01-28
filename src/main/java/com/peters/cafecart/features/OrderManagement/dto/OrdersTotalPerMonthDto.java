package com.peters.cafecart.features.OrderManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class OrdersTotalPerMonthDto {
    private Long ordersNumber;
    private BigDecimal totalPrice;
}
