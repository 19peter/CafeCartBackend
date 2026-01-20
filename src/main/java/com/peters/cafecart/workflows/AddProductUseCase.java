package com.peters.cafecart.workflows;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.AdditionsManagement.entity.AdditionGroup;
import com.peters.cafecart.features.AdditionsManagement.service.AdditionGroupServiceImpl;
import com.peters.cafecart.features.InventoryManagement.service.InventoryServiceImpl;
import com.peters.cafecart.features.ProductsManagement.entity.ProductOption;
import com.peters.cafecart.features.ProductsManagement.service.ProductOptionsServiceImpl;
import com.peters.cafecart.shared.dtos.Response.UploadUrlResponse;
import com.peters.cafecart.shared.services.S3.S3SignedUrlService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AddProductUseCase {
    @Autowired private ProductServiceImpl productService;
    @Autowired private VendorServiceImpl vendorService;
    @Autowired private ShopProductServiceImpl shopProductService;
    @Autowired private S3SignedUrlService s3SignedUrlService;
    @Autowired private InventoryServiceImpl inventoryService;
    @Autowired private ProductOptionsServiceImpl productOptionsService;
    @Autowired private AdditionGroupServiceImpl additionGroupService;
    @Transactional
    public AddProductResponseDto execute(AddProductRequestDto productDto, Long vendorId) {
        log.info("Starting AddProductUseCase for vendor {}: {}", vendorId, productDto.getName());
        Optional<VendorDto> vendorCheck = vendorService.getVendorById(vendorId);
        if (vendorCheck.isEmpty()) {
            log.warn("Vendor {} not found during product addition", vendorId);
            throw new ResourceNotFoundException("Vendor not found");
        }

        List<ProductOption> productOptions = productOptionsService.createProductOptionsForProduct(productDto.getOptions());
        List<AdditionGroup> additionGroups = additionGroupService.getAdditionGroupsByIds(vendorId, productDto.getAdditionGroupIds());
        if (additionGroups.size() != productDto.getAdditionGroupIds().size())
            throw new ValidationException("Addition(s) not available");
        AddProductResponseDto productResponseDto = productService.addProduct(productDto, vendorId, productOptions, additionGroups);
        log.info("Product created with ID: {} for vendor: {}", productResponseDto.getId(), vendorId);

        Set<Long> shopIds = vendorService.getShopIdsByVendorId(vendorId);
        log.debug("Found {} shops to synchronize product {} for vendor {}", shopIds.size(), productResponseDto.getId(), vendorId);
        
        shopProductService.addAProductToAllShops(productResponseDto.getId(), shopIds, productDto.getIsAvailable());
        if (productResponseDto.getIsStockTracked()) {
            log.debug("Initializing inventory for product {} across {} shops", productResponseDto.getId(), shopIds.size());
            inventoryService.createProductInventoryForAllShops(productResponseDto.getId(), shopIds);
        }
        
        String imageUrl = productDto.getImageUrl();
        String contentType = productDto.getContentType();
        if (imageUrl != null && contentType != null) {
            log.info("Generating S3 upload URL for product {} (Image: {})", productResponseDto.getId(), imageUrl);
            UploadUrlResponse urlResponse = s3SignedUrlService.generateUploadUrl(vendorId, imageUrl,contentType);
            productResponseDto.setFileUrl(urlResponse.getFileUrl());
            productResponseDto.setUploadUrl(urlResponse.getUploadUrl());
            productService.saveProductImage(productResponseDto.getId(), urlResponse.getFileUrl());
        }

        log.info("Successfully completed AddProductUseCase for product: {}", productResponseDto.getId());
        return productResponseDto;
    }


}
