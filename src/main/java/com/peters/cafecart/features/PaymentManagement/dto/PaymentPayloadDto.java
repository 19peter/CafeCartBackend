package com.peters.cafecart.features.PaymentManagement.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentPayloadDto {
    private double amount;
    private String currency = "EGP";
    private List<Integer> payment_methods;
    private List<PayloadItemDto> items;
    private PayloadBillingDetailsDto billing_data;
    private Map<String, Object> extras;
    private String special_reference;
    private int expiration;
    private String notification_url;
    private String redirection_url;
    private String private_key;
    public void setCurrency(String currency) {
        // this.currency = currency;
    }
}


