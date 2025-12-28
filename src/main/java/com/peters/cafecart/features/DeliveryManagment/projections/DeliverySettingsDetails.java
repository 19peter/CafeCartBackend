package com.peters.cafecart.features.DeliveryManagment.projections;

import com.peters.cafecart.shared.enums.DeliverySettingsEnum;

public interface DeliverySettingsDetails {
    Long getId();
    double getBaseFee();
    double getRatePerKm();
    DeliverySettingsEnum getDeliveryApproach();
}
