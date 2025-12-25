package com.peters.cafecart.features.OrderManagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrdersTotalPerMonthDto {
    private Long ordersNumber;
    private BigDecimal totalPrice;
}
