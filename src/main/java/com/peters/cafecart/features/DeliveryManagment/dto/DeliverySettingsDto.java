package com.peters.cafecart.features.DeliveryManagment.dto;
import com.peters.cafecart.shared.enums.DeliverySettingsEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeliverySettingsDto {
    double baseFee;
    double ratePerKM;
    DeliverySettingsEnum deliverySettingsEnum;
    List<DeliveryAreasDto> deliveryAreasDtoList;
    boolean isDeliveryAvailable;
}
