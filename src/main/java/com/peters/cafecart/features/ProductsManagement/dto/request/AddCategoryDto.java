package com.peters.cafecart.features.ProductsManagement.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCategoryDto {
    String name;
    Boolean isActive;
}
