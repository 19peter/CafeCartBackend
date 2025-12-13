package com.peters.cafecart.features.ShopProductManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopProductDto {
    private Long id;
    private Long productId;
    private Long vendorShopId;
    private Boolean isAvailable;
    private Integer quantity;
    private String name;
    private Double price;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private Boolean isStockTracked;
    private String description;
}
