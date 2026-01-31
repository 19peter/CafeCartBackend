package com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections;

public interface VendorShopIndexCover {
    // Covered by composite index: idx_vendor_id_name_address (vendor_id, name, address)
    Long getId();
    Long getVendorId();
    String getName();
    String getAddress();
    String getPhoneNumber();
    Boolean getIsVendorActive();
    Boolean getIsShopActive();
}
