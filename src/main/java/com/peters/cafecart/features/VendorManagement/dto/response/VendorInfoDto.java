package com.peters.cafecart.features.VendorManagement.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VendorInfoDto {
    Long id;
    String name;
    String email;
    String phoneNumber;
    Boolean isActive;
    LocalDateTime createdAt;
    Integer totalShops;
}
