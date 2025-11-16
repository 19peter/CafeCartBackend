package com.peters.cafecart.features.DeliveryManagment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerLocationRequestDto {
    private String latitude;
    private String longitude;
    private long shopId;
}
