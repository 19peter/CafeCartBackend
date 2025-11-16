package com.peters.cafecart.features.CustomerManagement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    
    CustomerDto toDto(Customer customer);
    
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Customer toEntity(CustomerDto customerDto);
}
