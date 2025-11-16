package com.peters.cafecart.features.OrderManagement.projections;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderSummaryShop {
    Long getId();
    String getOrderNumber();
 
    BigDecimal getTotalAmount();
    LocalDateTime getCreatedAt();
}
