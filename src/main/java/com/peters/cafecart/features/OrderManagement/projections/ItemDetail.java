package com.peters.cafecart.features.OrderManagement.projections;

public interface ItemDetail {
    Long getId();
    String getName();
    Integer getQuantity();
    String getSpecialInstructions();
}
