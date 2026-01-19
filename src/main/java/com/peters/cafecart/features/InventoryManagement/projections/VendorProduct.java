package com.peters.cafecart.features.InventoryManagement.projections;

public interface VendorProduct {
    Long getId();
    Long getVendorShopId();
    Long getProductId();
    Integer getQuantity();
    String getName();
//    Double getPrice();
}
