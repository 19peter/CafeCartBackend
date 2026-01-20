package com.peters.cafecart.features.ProductsManagement.dto.response;

import com.peters.cafecart.features.AdditionsManagement.dto.AdditionGroupDto;
import com.peters.cafecart.features.ProductsManagement.dto.BaseProductDto;

import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionDto;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionInformationDto;
import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class AddProductResponseDto extends BaseProductDto {
    @NonNull
    private Long id;
    private String uploadUrl;
    private String fileUrl;
    private ProductOptionInformationDto options;
    private List<AdditionGroupDto> additionGroups;

}
