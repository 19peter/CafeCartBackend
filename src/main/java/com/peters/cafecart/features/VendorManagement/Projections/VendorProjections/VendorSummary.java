package com.peters.cafecart.features.VendorManagement.Projections.VendorProjections;

public interface VendorSummary {
    Long getId();
    String getName();
    String getEmail();
    String getPhoneNumber();
    String getImageUrl();
    Boolean getIsActive();
}
