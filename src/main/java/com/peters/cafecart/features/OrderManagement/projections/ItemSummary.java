package com.peters.cafecart.features.OrderManagement.projections;

public interface ItemSummary {
    Long getId();
    String getProductId();
    String getName();
    Integer getQuantity();
}
