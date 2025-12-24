package com.peters.cafecart.workflows;

import java.util.Optional;
import java.util.Set;

import com.peters.cafecart.features.InventoryManagement.service.InventoryServiceImpl;
import com.peters.cafecart.shared.dtos.Response.UploadUrlResponse;
import com.peters.cafecart.shared.services.S3SignedUrlService;
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
    @Autowired private S3SignedUrlService s3SignedUrlService;
    @Autowired private InventoryServiceImpl inventoryService;

    @Transactional
    public AddProductResponseDto execute(AddProductRequestDto productDto, Long vendorId) {
        Optional<VendorDto> vendorCheck = vendorService.getVendorById(vendorId);
        if (vendorCheck.isEmpty())
            throw new ResourceNotFoundException("Vendor not found");

        AddProductResponseDto productResponseDto = productService.addProduct(productDto, vendorId);

        Set<Long> shopIds = vendorService.getShopIdsByVendorId(vendorId);
        shopProductService.addAProductToAllShops(productResponseDto.getId(), shopIds, productDto.getIsAvailable());
        if (productResponseDto.getIsStockTracked()) {
            inventoryService.createProductInventoryForAllShops(productResponseDto.getId(), shopIds);
        }
        String imageUrl = productDto.getImageUrl();
        String contentType = productDto.getContentType();
        if (imageUrl != null && contentType != null) {
            UploadUrlResponse urlResponse = s3SignedUrlService.generateUploadUrl(vendorId, imageUrl,contentType);
            productResponseDto.setFileUrl(urlResponse.getFileUrl());
            productResponseDto.setUploadUrl(urlResponse.getUploadUrl());
            productService.saveProductImage(productResponseDto.getId(), urlResponse.getFileUrl());
        }

        return productResponseDto;
    }


}
