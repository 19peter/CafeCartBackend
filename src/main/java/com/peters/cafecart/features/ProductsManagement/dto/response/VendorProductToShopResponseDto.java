package com.peters.cafecart.features.ProductsManagement.dto.response;

import com.peters.cafecart.features.ProductsManagement.dto.BaseProductDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorProductToShopResponseDto extends BaseProductDto{
    private Long id;
    private Boolean isOwnedByShop;
    
}
