package com.peters.cafecart.features.CustomerManagement.service;

import com.peters.cafecart.features.CustomerManagement.dto.AddressDto;
import com.peters.cafecart.features.CustomerManagement.dto.CustomerBasicInfoDto;
import com.peters.cafecart.features.CustomerManagement.dto.PhoneDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.CustomerManagement.repository.CustomerRepository;
import com.peters.cafecart.features.CustomerManagement.mapper.CustomerMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

   @Autowired CustomerRepository customerRepository;
   @Autowired CustomerMapper customerMapper;
   @Autowired PasswordEncoder passwordEncoder;

   @Override
   public Customer getCustomerById(Long id) {
      if (id == null) throw new ValidationException("Customer ID cannot be null");
      return customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
   }

   @Override
   public Customer createCustomer(CustomerDto customerDto) {
      if (customerRepository.findByEmail(customerDto.getEmail()).isPresent()) throw new ValidationException("Email already exists");
      if (customerRepository.findByPhoneNumber(customerDto.getPhoneNumber()).isPresent()) throw new ValidationException("Phone number already exists");

      Customer customer = customerMapper.toEntity(customerDto);
      customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
         try {
         return customerRepository.save(customer);
      } catch (Exception e) {
         throw new ValidationException("Customer not created");
      }
   }

   @Override
   public CustomerBasicInfoDto getCustomerBasicInfo(Long id) {
      Customer customer = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
      CustomerBasicInfoDto dto = new CustomerBasicInfoDto();
      dto.setAddress(customer.getAddress());
      dto.setFirstName(customer.getFirstName());
      dto.setLastName(customer.getLastName());
      dto.setPhoneNumber(customer.getPhoneNumber());
      return dto;
   }

   @Override
   public void updateAddress(Long id, AddressDto address) {
      Customer customer = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
      if (address.getAddress().isEmpty())  throw new ValidationException("Invalid Address");
      customer.setAddress(address.getAddress());
      customerRepository.save(customer);
   }

   @Override
   public void updatePhone(Long id, PhoneDto phoneDto) {
      Customer customer = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
      if (!isValidEgyptianMobile(phoneDto.getPhone())) throw new ValidationException("Invalid Phone Number");
      Optional<Customer> existingCheck = customerRepository.findByPhoneNumber(phoneDto.getPhone());
      if (existingCheck.isPresent())
         throw new ValidationException("Phone Number is already in use");
      customer.setPhoneNumber(phoneDto.getPhone());
      customerRepository.save(customer);
   }


   private static boolean isValidEgyptianMobile(String phone) {
      if (phone == null) {
         return false;
      }

      // remove spaces, dashes, parentheses
      phone = phone.replaceAll("[\\s\\-()]", "");

      // normalize international formats
      if (phone.startsWith("+20")) {
         phone = phone.substring(3);
      } else if (phone.startsWith("20")) {
         phone = phone.substring(2);
      }

      // must be exactly 11 digits
      if (!phone.matches("\\d{11}")) {
         return false;
      }

      // must start with 01
      if (!phone.startsWith("01")) {
         return false;
      }

      // valid operator prefixes
      char operatorDigit = phone.charAt(2);
      return operatorDigit == '0'
              || operatorDigit == '1'
              || operatorDigit == '2'
              || operatorDigit == '5';
   }

}
