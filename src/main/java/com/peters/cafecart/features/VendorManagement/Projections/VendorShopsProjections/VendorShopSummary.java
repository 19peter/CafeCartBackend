package com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections;

import java.sql.Time;

import com.peters.cafecart.features.VendorManagement.Projections.VendorProjections.VendorIdName;

public interface VendorShopSummary {
    Long getId();
    String getName();
    String getAddress();
    String getPhoneNumber();
    String getEmail();
    String getLogoUrl();
    Boolean getIsActive();
    boolean getIsDeliveryAvailable();
    Time getOpeningTime();
    Time getClosingTime();
    Time getLastOrderTime();

    // Nested projection to expose the vendor id without loading entire entity
    VendorIdName getVendor();
}
