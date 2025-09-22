package com.peters.cafecart.features.VendorManagement.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopIndexCover;
import com.peters.cafecart.features.VendorManagement.Projections.VendorShopsProjections.VendorShopSummary;
import com.peters.cafecart.features.VendorManagement.dto.VendorIdNameDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopIndexCoverDto;
import com.peters.cafecart.features.VendorManagement.dto.VendorShopSummaryDto;

@Mapper(componentModel = "spring")
public interface VendorShopMappers {

    // Map flat index projection
    VendorShopIndexCoverDto toDto(VendorShopIndexCover projection);

    // Map summary including nested vendor minimal info
    @Mapping(target = "vendor", source = "vendor")
    VendorShopSummaryDto toDto(VendorShopSummary projection);

    // Map nested vendor id+name projection to DTO
    VendorIdNameDto toDto(com.peters.cafecart.features.VendorManagement.Projections.VendorProjections.VendorIdName vendor);

    // List helpers
    default List<VendorShopIndexCoverDto> toIndexCoverList(List<VendorShopIndexCover> list) {
        return list.stream().map(this::toDto).toList();
    }

    default List<VendorShopSummaryDto> toSummaryList(List<VendorShopSummary> list) {
        return list.stream().map(this::toDto).toList();
    }
}
