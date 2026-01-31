package com.peters.cafecart.features.ShopProductManagement.projection;

public interface ShopProductStock {
    Long getId();
    Long getVendorShopId();
    Long getProductId();
    Integer getQuantity();
    String getName();
    Double getPrice();
    String getImageUrl();
    Long getCategoryId();
    String getCategoryName();
    Boolean getIsStockTracked();
    Boolean getIsAvailable();
    String getDescription();
    Boolean getIsShopActive();
    Boolean getIsVendorActive();
}
