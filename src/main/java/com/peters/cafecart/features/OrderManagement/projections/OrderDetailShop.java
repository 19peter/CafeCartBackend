package com.peters.cafecart.features.OrderManagement.projections;

import java.time.LocalDateTime;

import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;
import com.peters.cafecart.shared.enums.OrderTypeEnum;

public interface OrderDetailShop {
    Long getId();
    String getOrderNumber();
    String getDeliveryAddress();
    OrderTypeEnum getOrderType();
    OrderStatusEnum getStatus();
    LocalDateTime getCreatedAt(); 
    Integer getItemCount();
}
