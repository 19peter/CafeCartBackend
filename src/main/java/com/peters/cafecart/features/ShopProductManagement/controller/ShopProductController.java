package com.peters.cafecart.features.ShopProductManagement.controller;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.features.ShopProductManagement.service.ShopProductServiceImpl;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import com.peters.cafecart.features.ShopProductManagement.dto.ShopProductDto;
import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.peters.cafecart.workflows.GetProductDetailsUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.CURRENT_API + "/shop-products")
public class ShopProductController {

    @Autowired ShopProductServiceImpl shopProductService;
    @Autowired ProductServiceImpl productService;
    @Autowired VendorShopsServiceImpl vendorShopsService;
    @Autowired GetProductDetailsUseCase getProductDetailsUseCase;

    @GetMapping("/{vendorShopId}")
    public List<ShopProductDto> findAllByVendorShopIdAndIsAvailableTrue(
            @PathVariable Long vendorShopId) {
        return shopProductService.findAllByVendorShopIdAndIsAvailableTrue(vendorShopId);
    }

    @GetMapping("/{vendorShopId}/product/{productId}")
    public ShopProductDto findByProductAndVendorShop(
            @PathVariable Long vendorShopId,
            @PathVariable Long productId) {
        return getProductDetailsUseCase.execute(productId,vendorShopId);
    }

    @GetMapping("/shop")
    public List<ShopProductDto> findAllForVendorShop(@AuthenticationPrincipal CustomUserPrincipal user) {
        return shopProductService.findAllForVendorShop(user.getId());
    }

    @PostMapping("/shop/publish/product/{productId}")
    public boolean publishShopProduct(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @PathVariable Long productId) {

        validateVendorShopAndProduct(user.getId(), productId);
        return shopProductService.publishShopProduct(productId, user.getId());
    }

    @PostMapping("/shop/unpublish/product/{productId}")
    public boolean unpublishShopProduct(
            @AuthenticationPrincipal CustomUserPrincipal user,
            @PathVariable Long productId) {
        validateVendorShopAndProduct(user.getId(), productId);
        return shopProductService.unpublishShopProduct(productId, user.getId());
    }

    private void validateVendorShopAndProduct(Long vendorShopId, Long productId) {
        if (vendorShopId == null || productId == null)
            throw new ValidationException("Vendor Shop ID or Product ID cannot be null");
        VendorShop vendorShop = vendorShopsService.getVendorShop(vendorShopId)
                .orElseThrow(() -> new ValidationException("Vendor Shop ID cannot be found"));
        Vendor vendor = vendorShop.getVendor();
        if (vendor == null || !vendor.getIsActive())
            throw new ValidationException("Invalid Operation");
        Optional<Product> productCheck = productService.getProductById(productId);
        if (productCheck.isEmpty())
            throw new ValidationException("Product ID cannot be found");

        if (!Objects.equals(productCheck.get().getVendor().getId(), vendorShop.getVendor().getId()))
            throw new ValidationException("Invalid Operation");
    }
}
