package com.peters.cafecart.features.AdditionsManagement.dto;

import lombok.Data;

@Data
public class ShopAdditionDto {
    private Long id;
    private Long shopId;
    private AdditionDto addition;
    private Boolean isAvailable;
}
