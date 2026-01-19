package com.peters.cafecart.features.ProductsManagement.dto.response;

import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionDto;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionInformationDto;
import lombok.Getter;
import lombok.Setter;
import com.peters.cafecart.features.ProductsManagement.dto.BaseProductDto;

import java.util.List;

@Getter
@Setter
public class UpdateProductResponseDto extends BaseProductDto{
    private Long id;
    private String uploadUrl;
    private String fileUrl;
//    private List<ProductOptionDto> optionDtoList;
    private ProductOptionInformationDto options;

}
