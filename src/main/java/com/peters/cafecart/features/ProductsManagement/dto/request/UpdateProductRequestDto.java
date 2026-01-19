package com.peters.cafecart.features.ProductsManagement.dto.request;

import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionDto;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionInformationDto;
import lombok.Getter;
import lombok.Setter;
import com.peters.cafecart.features.ProductsManagement.dto.BaseProductDto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

@Getter
@Setter
public class UpdateProductRequestDto extends BaseProductDto{
    @NotNull
    private Long id;
    private String contentType;
    private ProductOptionInformationDto options;
}
