package com.peters.cafecart.features.ShopProductManagement.projection;

public interface ShopProductAvailabilityView {
    boolean getIsAvailable();
    boolean getIsStockTracked();
    int getQuantity();
}
