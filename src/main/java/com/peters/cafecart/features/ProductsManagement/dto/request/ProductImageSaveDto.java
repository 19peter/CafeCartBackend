package com.peters.cafecart.features.ProductsManagement.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageSaveDto {
    private Long productId;
    private String uploadUrl;
}
