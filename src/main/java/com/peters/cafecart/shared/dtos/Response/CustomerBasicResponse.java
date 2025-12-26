package com.peters.cafecart.shared.dtos.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerBasicResponse {
    Long id;
    String firstName;
    String lastName;
    String phoneNumber;
}
