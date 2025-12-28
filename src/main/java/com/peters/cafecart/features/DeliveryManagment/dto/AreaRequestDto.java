package com.peters.cafecart.features.DeliveryManagment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AreaRequestDto {
    Long id;
    String area;
    String city;
    BigDecimal price;
}
