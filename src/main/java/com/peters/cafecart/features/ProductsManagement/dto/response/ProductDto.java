package com.peters.cafecart.features.ProductsManagement.dto.response;

import com.peters.cafecart.features.ProductsManagement.dto.BaseProductDto;

import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionInformationDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto extends BaseProductDto {
    private Long id;
    private ProductOptionInformationDto options;
}
