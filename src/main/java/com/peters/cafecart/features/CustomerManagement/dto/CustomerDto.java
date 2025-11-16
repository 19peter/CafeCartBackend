package com.peters.cafecart.features.CustomerManagement.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate dob;
    private String phoneNumber;
}
