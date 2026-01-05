package com.peters.cafecart.features.VendorManagement.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVendorDto {
    String email;
    String name;
    String phoneNumber;
    String vaaEmail;
    String vaaPassword;
    String imageUrl;
    String contentType;
}
