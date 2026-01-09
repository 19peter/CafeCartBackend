package com.peters.cafecart.workflows;

import java.util.List;
import java.util.stream.Collectors;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.DeliveryManagment.service.DeliveryServiceImpl;
import com.peters.cafecart.features.ProductsManagement.dto.BaseProductDto;
import com.peters.cafecart.features.VendorManagement.entity.Vendor;
import com.peters.cafecart.features.VendorManagement.service.VendorServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import com.peters.cafecart.features.ShopProductManagement.service.ShopProductServiceImpl;

import jakarta.transaction.Transactional;

import com.peters.cafecart.features.InventoryManagement.service.InventoryServiceImpl;
import com.peters.cafecart.features.ProductsManagement.dto.response.ProductDto;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.ShopManagement.dto.AddShopDto;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;

@Service
@Slf4j
public class AddShopUseCase {
    /// 1- Create a Shop for a Vendor
    /// 2- Add Products to the Shop (ProductShop)
    /// 3- Add Inventory to the Shop (Inventory)
    /// 4- Create Default Delivery Settings for shop
    @Autowired VendorShopsServiceImpl vendorShopsService;
    @Autowired InventoryServiceImpl inventoryService;
    @Autowired ShopProductServiceImpl shopProductService;
    @Autowired ProductServiceImpl productService;
    @Autowired DeliveryServiceImpl deliveryService;
    @Autowired VendorServiceImpl vendorService;

    @Transactional
    public void execute(AddShopDto addShopDto, Long vendorId) {
        log.info("Starting AddShopUseCase for vendor {}: {}", vendorId, addShopDto.getName());
        Vendor vendor = vendorService.getVendor(vendorId).orElseThrow(() -> {
            log.warn("Vendor {} not found during shop addition", vendorId);
            return new ResourceNotFoundException("Vendor not found");
        });
        
        if (vendor.getShops().size() >= 2) {
            log.warn("Vendor {} already has {} shops. Limit reached.", vendorId, vendor.getShops().size());
            throw new ValidationException("Maximum number of shops reached");
        }
        
        VendorShop vendorShop = vendorShopsService.addShop(addShopDto, vendorId);
        log.info("Shop created with ID: {} for vendor: {}", vendorShop.getId(), vendorId);
        
        List<ProductDto> products = productService.getProductsForVendorShopByVendorId(vendorId);
        log.debug("Synchronizing {} products to new shop {}", products.size(), vendorShop.getId());
        
        shopProductService.addAllProductsToAShop(vendorShop.getId(), products.stream().map(ProductDto::getId).collect(Collectors.toSet()), false);
        
        List<ProductDto> stockTrackedProducts = products.stream().filter(BaseProductDto::getIsStockTracked).toList();
        log.debug("Found {} stock-tracked products to initialize inventory for shop {}", stockTrackedProducts.size(), vendorShop.getId());
        
        for (ProductDto product : stockTrackedProducts) {
            inventoryService.createInventory(vendorShop.getId(), product.getId(), 0);
        }
        
        log.debug("Initializing default delivery settings for shop {}", vendorShop.getId());
        deliveryService.createDefaultDeliverySettingsForShop(vendorShop);
        
        log.info("Successfully completed AddShopUseCase for shop: {}", vendorShop.getId());
    }
}
