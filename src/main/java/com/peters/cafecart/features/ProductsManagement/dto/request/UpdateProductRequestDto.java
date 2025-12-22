package com.peters.cafecart.features.ProductsManagement.dto.request;

import lombok.Getter;
import lombok.Setter;
import com.peters.cafecart.features.ProductsManagement.dto.BaseProductDto;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateProductRequestDto extends BaseProductDto{
    @NotNull
    private Long id;
    private String contentType;

    
}
