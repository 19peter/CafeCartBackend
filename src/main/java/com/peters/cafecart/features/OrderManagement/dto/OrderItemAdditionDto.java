package com.peters.cafecart.features.OrderManagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemAdditionDto {
    private Long id;
    private Long additionId;
    private String name;
    private BigDecimal price;
}
