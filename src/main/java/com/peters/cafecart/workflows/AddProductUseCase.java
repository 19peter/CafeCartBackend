package com.peters.cafecart.workflows;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.ProductsManagement.dto.request.AddProductRequestDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.AddProductResponseDto;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.ShopProductManagement.service.ShopProductServiceImpl;
import com.peters.cafecart.features.VendorManagement.dto.VendorDto;
import com.peters.cafecart.features.VendorManagement.service.VendorServiceImpl;

@Service
public class AddProductUseCase {
    @Autowired private ProductServiceImpl productService;
    @Autowired private VendorServiceImpl vendorService;
    @Autowired private ShopProductServiceImpl shopProductService;

    @Transactional
    public AddProductResponseDto execute(AddProductRequestDto productDto, Long vendorId) {
        Optional<VendorDto> vendorCheck = vendorService.getVendorById(vendorId);
        if (vendorCheck.isEmpty())
            throw new ResourceNotFoundException("Vendor not found");

        AddProductResponseDto productResponseDto = productService.addProduct(productDto, vendorId);
        Set<Long> shopIds = vendorService.getShopIdsByVendorId(vendorId);
        shopProductService.addAProductToAllShops(productResponseDto.getId(), shopIds, productDto.getIsAvailable());
        return productResponseDto;
    }


}
