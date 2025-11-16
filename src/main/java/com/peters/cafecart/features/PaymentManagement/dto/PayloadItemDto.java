package com.peters.cafecart.features.PaymentManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayloadItemDto {
    private String name;
    private double amount;
    private String description;
    private int quantity;
}
