package com.peters.cafecart.features.ProductsManagement.dto.response;

import lombok.Getter;
import lombok.Setter;
import com.peters.cafecart.features.ProductsManagement.dto.BaseProductDto;

@Getter
@Setter
public class UpdateProductResponseDto extends BaseProductDto{
    private Long id;
    
}
