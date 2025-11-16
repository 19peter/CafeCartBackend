package com.peters.cafecart.features.CustomerManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.CustomerManagement.repository.CustomerRepository;
import com.peters.cafecart.features.CustomerManagement.mapper.CustomerMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class CustomerServiceImpl implements CustomerService {

   @Autowired private CustomerRepository customerRepository;
   @Autowired private CustomerMapper customerMapper;
   @Autowired private PasswordEncoder passwordEncoder;

   @Override
   public Customer getCustomerById(Long id) {
      return customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
   }

   @Override
   public void createCustomer(CustomerDto customerDto) {
      if (customerRepository.findByEmail(customerDto.getEmail()).isPresent()) throw new ValidationException("Email already exists");
      if (customerRepository.findByPhoneNumber(customerDto.getPhoneNumber()).isPresent()) throw new ValidationException("Phone number already exists");

      Customer customer = customerMapper.toEntity(customerDto);
      customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
         try {
         customerRepository.save(customer);
      } catch (Exception e) {
         throw new ValidationException("Customer not created");
      }
   }
}
