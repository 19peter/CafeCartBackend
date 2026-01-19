package com.peters.cafecart.workflows;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.ProductsManagement.dto.response.ProductDto;
import com.peters.cafecart.features.ProductsManagement.dto.response.VendorProductToShopResponseDto;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.ShopManagement.repository.VendorShopsRepository;
import com.peters.cafecart.features.ShopProductManagement.service.ShopProductServiceImpl;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;

import jakarta.transaction.Transactional;

@Service
@Slf4j
public class CreateVendorShopProductsUseCase {
    @Autowired ProductServiceImpl productService;
    @Autowired ShopProductServiceImpl shopProductsService;
    @Autowired VendorShopsRepository vendorShopsRepository;

    @Transactional
    public List<VendorProductToShopResponseDto> execute(Long vendorShopId) {
        log.debug("Executing CreateVendorShopProductsUseCase for shop ID: {}", vendorShopId);
        if (vendorShopId == null) {
            log.warn("Attempted to fetch products with null shop ID");
            throw new ValidationException("Vendor Shop ID cannot be null");
        }
        VendorShop vendorShop = vendorShopsRepository.findById(vendorShopId)
                .orElseThrow(() -> {
                    log.warn("Shop {} not found during product list creation", vendorShopId);
                    return new ValidationException("Vendor Shop ID cannot be found");
                });
        Vendor vendor = vendorShop.getVendor();
        if (vendor == null || !vendor.getIsActive()) {
            log.warn("Attempted to process products for inactive or null vendor linked to shop {}", vendorShopId);
            throw new ValidationException("Invalid Operation");
        }

        log.debug("Fetching products for vendor {} and shop {}", vendor.getId(), vendorShopId);
        List<ProductDto> products = productService.getProductsForVendorShopByVendorId(vendor.getId());
        Set<Long> shopProductIds = shopProductsService.findAllProductIdsForVendorShop(vendorShopId);

        log.info("Returning {} products ({} owned by shop) for shop {}", products.size(), shopProductIds.size(), vendorShopId);
        return products.stream()
                .map(product -> {
                    boolean isOwnedByShop = shopProductIds.contains(product.getId());
                    return toVendorProductToShopResponseDto(product, isOwnedByShop);
                })
                .collect(Collectors.toList());
    }

    private VendorProductToShopResponseDto toVendorProductToShopResponseDto(ProductDto productDto,
            Boolean isOwnedByShop) {
        VendorProductToShopResponseDto vendorProductToShopResponseDto = new VendorProductToShopResponseDto();
        vendorProductToShopResponseDto.setId(productDto.getId());
        vendorProductToShopResponseDto.setName(productDto.getName());
        vendorProductToShopResponseDto.setImageUrl(productDto.getImageUrl());
        vendorProductToShopResponseDto.setCategoryId(productDto.getCategoryId());
        vendorProductToShopResponseDto.setCategoryName(productDto.getCategoryName());
        vendorProductToShopResponseDto.setIsStockTracked(productDto.getIsStockTracked());
        vendorProductToShopResponseDto.setDescription(productDto.getDescription());
        vendorProductToShopResponseDto.setIsOwnedByShop(isOwnedByShop);
        return vendorProductToShopResponseDto;
    }
}
