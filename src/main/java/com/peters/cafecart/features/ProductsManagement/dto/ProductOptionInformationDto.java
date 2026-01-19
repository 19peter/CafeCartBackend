package com.peters.cafecart.features.ProductsManagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductOptionInformationDto {
    private List<ProductOptionDto> optionList = new ArrayList<>();
    private Boolean hasDefaultSize;

}
