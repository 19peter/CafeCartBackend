package com.peters.cafecart.features.CartManagement.dto.base;

import com.peters.cafecart.shared.enums.OrderTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class OrderTypeBase {
    OrderTypeEnum orderType;
}
