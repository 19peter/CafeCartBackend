package com.peters.cafecart.features.ShopProductManagement.controller;
import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.features.ShopProductManagement.service.ShopProductServiceImpl;
import com.peters.cafecart.features.ShopProductManagement.dto.ShopProductDto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.CURRENT_API + "/shop-products")
public class ShopProductController {
    
    @Autowired ShopProductServiceImpl shopProductService;

    @GetMapping("/vendor/{vendorShopId}")
    public List<ShopProductDto> findAllByVendorShopIdAndIsAvailableTrue(
            @PathVariable Long vendorShopId) {
        return shopProductService.findAllByVendorShopIdAndIsAvailableTrue(vendorShopId);
    }

    @GetMapping("/vendor/{vendorShopId}/product/{productId}")
    public ShopProductDto findByProductAndVendorShop(
            @PathVariable Long vendorShopId,
            @PathVariable Long productId) {
        return shopProductService.findByProductAndVendorShop(productId, vendorShopId);
    }
}
