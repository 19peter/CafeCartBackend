package com.peters.cafecart.features.CartManagement.dto.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PickupOrderTypeDto extends OrderTypeBase {
    String pickupTime;
}
