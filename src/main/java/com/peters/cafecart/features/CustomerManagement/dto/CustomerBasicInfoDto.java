package com.peters.cafecart.features.CustomerManagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class CustomerBasicInfoDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
}
