package com.peters.cafecart.features.ProductsManagement.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseProductDto {
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private Long vendorId;
    private String imageUrl;
    private Boolean isStockTracked;
    private Boolean isAvailable;
}
