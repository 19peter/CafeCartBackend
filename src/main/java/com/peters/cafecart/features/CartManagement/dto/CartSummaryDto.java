package com.peters.cafecart.features.CartManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartSummaryDto {
    private Long id;
    private Long customerId;
    private Long shopId;
    private Long vendorId;
}
