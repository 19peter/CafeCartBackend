package com.peters.cafecart.features.VerifiedCustomerManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifiedCustomerDto {
    Long customerId;
    Long vendorId;
    Long verifiedById;
    boolean isVerified;
}
