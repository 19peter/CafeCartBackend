package com.peters.cafecart.features.OrderManagement.projections;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderDetailCustomer {
    Long getId();
    String getOrderNumber();
    String getStatus();
    BigDecimal getTotalAmount();
    BigDecimal getDeliveryFee();
    BigDecimal getTotalPrice();
    LocalDateTime getCreatedAt();
    List<ItemDetail> getItems();
}
