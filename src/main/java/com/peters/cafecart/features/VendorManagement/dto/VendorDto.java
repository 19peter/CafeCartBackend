package com.peters.cafecart.features.VendorManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorDto {
    public Long id;
    public String name;
    public String email;
    public String phoneNumber;
    public String imageUrl;
    public Boolean isActive;
}
