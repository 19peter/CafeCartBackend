package com.peters.cafecart.features.InventoryManagement.mappers;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import com.peters.cafecart.features.InventoryManagement.dto.VendorProductDto;
import com.peters.cafecart.features.InventoryManagement.projections.VendorProduct;

@Mapper(componentModel = "spring")
public interface InventoryMappers {
    VendorProductDto toDto(VendorProduct projection);

    default Page<VendorProductDto> toDtoPage(Page<VendorProduct> page) {
        return page.map(this::toDto);
    }

}
