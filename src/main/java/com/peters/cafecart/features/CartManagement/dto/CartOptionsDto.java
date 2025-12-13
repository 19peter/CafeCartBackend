package com.peters.cafecart.features.CartManagement.dto;

import com.peters.cafecart.shared.enums.OrderTypeEnum;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartOptionsDto {
    private OrderTypeEnum orderType;
    private PaymentMethodEnum paymentMethod;
    private String latitude;
    private String longitude;
    private String address;
    private String pickupTime;
}
