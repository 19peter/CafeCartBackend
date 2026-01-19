package com.peters.cafecart.features.ProductsManagement.dto;

import com.peters.cafecart.shared.enums.ProductSizes;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class ProductOptionDto {
    Long id;
    ProductSizes size;
    BigDecimal price;
    Boolean isDeleted;

   public ProductOptionDto(ProductSizes size, BigDecimal price) {
        this.price = price;
        this.size = size;
   }
}
