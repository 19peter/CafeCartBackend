package com.peters.cafecart.features.VendorManagement.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedVendorDto {
    Long id;
    String name;
    String email;
    String phoneNumber;
    String vaaEmail;
}
