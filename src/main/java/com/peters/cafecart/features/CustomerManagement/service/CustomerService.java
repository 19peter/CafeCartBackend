package com.peters.cafecart.features.CustomerManagement.service;


import org.springframework.stereotype.Service;

import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;

@Service
public interface CustomerService {
    public Customer getCustomerById(Long id);
    public void createCustomer(CustomerDto customerDto);
}
