package com.peters.cafecart.features.PaymentManagement.projections;

public interface VendorPaymentInfo {
    Long getVendorId();
    int getIntegrationId();
    String getPrivateKey();
    String getPublicKey();
}
