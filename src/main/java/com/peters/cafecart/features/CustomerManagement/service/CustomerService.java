package com.peters.cafecart.features.CustomerManagement.service;


import com.peters.cafecart.features.CustomerManagement.dto.AddressDto;
import com.peters.cafecart.features.CustomerManagement.dto.CustomerBasicInfoDto;
import com.peters.cafecart.features.CustomerManagement.dto.PhoneDto;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;

@Service
public interface CustomerService {
    Customer getCustomerById(Long id);
    Customer createCustomer(CustomerDto customerDto);

    CustomerBasicInfoDto getCustomerBasicInfo(Long id);
    void updateAddress(Long id, AddressDto address);
    void updatePhone(Long id, PhoneDto phoneDto);

}
