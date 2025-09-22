package com.peters.cafecart.features.VendorManagement.dto;

import java.sql.Time;

public class VendorShopSummaryDto {
    public Long id;
    public String name;
    public String address;
    public String phoneNumber;
    public String email;
    public String logoUrl;
    public Boolean isActive;
    public boolean isDeliveryAvailable;
    public Time openingTime;
    public Time closingTime;
    public Time lastOrderTime;

    // Nested vendor minimal info
    public VendorIdNameDto vendor;
}
