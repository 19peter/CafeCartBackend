package com.peters.cafecart.features.VendorManagement.mappers;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import com.peters.cafecart.features.VendorManagement.Projections.VendorProjections.VendorIdName;
import com.peters.cafecart.features.VendorManagement.Projections.VendorProjections.VendorSummary;
import com.peters.cafecart.features.VendorManagement.dto.VendorDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorIdNameDto;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;

@Mapper(componentModel = "spring")
public interface VendorMappers {
    // Single-item mappings
    VendorIdNameDto toDto(VendorIdName projection);
    VendorDto toDto(VendorSummary projection);
    VendorDto toDto(Vendor entity);

    // Page mappings using Spring Data's Page.map
    default Page<VendorIdNameDto> toDtoPageIdName(Page<VendorIdName> page) {
        return page.map(this::toDto);
    }

    default Page<VendorDto> toDtoPageSummary(Page<VendorSummary> page) {
        return page.map(this::toDto);
    }
}
