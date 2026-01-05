package com.peters.cafecart.features.CartManagement.dto;

import com.peters.cafecart.shared.enums.OrderTypeEnum;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartSummaryDto {
    private Long id;
    private Long customerId;
    private Long shopId;
    private Long vendorId;
    private boolean isDeliveryAvailable;
    private boolean isOnlinePaymentAvailable;
    private boolean isOnline;
    private boolean isVerified;
    private List<PaymentMethodEnum> allowedPaymentMethods;
}
