package com.peters.cafecart.features.ShopProductManagement.dto;

import com.peters.cafecart.features.AdditionsManagement.dto.AdditionGroupDto;
import com.peters.cafecart.features.ProductsManagement.dto.ProductOptionDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ShopProductDto {
    private Long id;
    private Long productId;
    private Long vendorShopId;
    private Boolean isAvailable;
    private Integer quantity;
    private String name;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private Boolean isStockTracked;
    private Boolean isShopActive;
    private Boolean isVendorActive;
    private String description;
    List<ProductOptionDto> options = new ArrayList<>();
    List<AdditionGroupDto> additionGroups = new ArrayList<>();
}
