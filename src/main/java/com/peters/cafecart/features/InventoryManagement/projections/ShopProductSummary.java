package com.peters.cafecart.features.InventoryManagement.projections;

public interface ShopProductSummary {
    Long getProductId();
    Long getVendorShopId();
    Integer getQuantity();
}
