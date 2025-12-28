package com.peters.cafecart.features.CartManagement.dto.base;

import com.peters.cafecart.features.DeliveryManagment.dto.DeliveryAreasDto;
import com.peters.cafecart.shared.enums.DeliverySettingsEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeliveryOrderTypeDto extends OrderTypeBase {
    Long deliveryAreaId;
    String deliveryAreaName;
    double price;
    String address;
    DeliverySettingsEnum deliverySettingsEnum;
    List<DeliveryAreasDto> availableDeliveryAreas;
}
