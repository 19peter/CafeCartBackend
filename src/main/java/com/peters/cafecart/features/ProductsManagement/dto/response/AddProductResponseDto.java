package com.peters.cafecart.features.ProductsManagement.dto.response;

import com.peters.cafecart.features.ProductsManagement.dto.BaseProductDto;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class AddProductResponseDto extends BaseProductDto {
    @NonNull
    private Long id;
    private String uploadUrl;
    private String fileUrl;
}
