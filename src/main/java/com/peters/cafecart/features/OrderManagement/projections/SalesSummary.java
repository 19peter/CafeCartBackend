package com.peters.cafecart.features.OrderManagement.projections;

import java.math.BigDecimal;

public interface SalesSummary {
    Long getCount();
    BigDecimal getTotal();
}
