package com.peters.cafecart.features.OrderManagement.dto;

import com.peters.cafecart.features.OrderManagement.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentStatusUpdate {
    Long orderId;
    PaymentStatus paymentStatus;
}
