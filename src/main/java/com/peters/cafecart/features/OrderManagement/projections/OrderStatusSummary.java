package com.peters.cafecart.features.OrderManagement.projections;

import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;
import com.peters.cafecart.shared.enums.OrderTypeEnum;

public interface OrderStatusSummary {
    Long getVendorShopId();
    OrderStatusEnum getStatus();
    OrderTypeEnum getOrderType();
}
