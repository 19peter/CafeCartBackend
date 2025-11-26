package com.peters.cafecart.features.OrderManagement.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {
    private Long id;
    private String name;
    private Integer quantity;
    private BigDecimal price;
    private String specialInstructions;
}
