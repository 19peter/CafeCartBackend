package com.peters.cafecart.features.PaymentManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayloadBillingDetailsDto {
    private String apartment;
    private String first_name;
    private String last_name;
    private String street;
    private String building;
    private String phone_number;
    private String city;
    private String country;
    private String email;
    private String floor;
    private String state;
}
