package com.peters.cafecart.features.ProductsManagement.dto.request;

import com.peters.cafecart.features.ProductsManagement.dto.BaseProductDto;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionDto;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionInformationDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddProductRequestDto extends BaseProductDto {
    private String contentType;
    private ProductOptionInformationDto options;
}
