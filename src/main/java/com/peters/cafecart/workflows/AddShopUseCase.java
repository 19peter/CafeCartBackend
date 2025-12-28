package com.peters.cafecart.workflows;

import java.util.List;
import java.util.stream.Collectors;

import com.peters.cafecart.features.DeliveryManagment.service.DeliveryServiceImpl;
import com.peters.cafecart.features.ProductsManagement.dto.BaseProductDto;
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

    @Transactional
    public void execute(AddShopDto addShopDto, Long vendorId) {
        VendorShop vendorShop = vendorShopsService.addShop(addShopDto, vendorId);
        List<ProductDto> products = productService.getProductsForVendorShopByVendorId(vendorId);
        shopProductService.addAllProductsToAShop(vendorShop.getId(), products.stream().map(ProductDto::getId).collect(Collectors.toSet()), false);
        List<ProductDto> stockTrackedProducts = products.stream().filter(BaseProductDto::getIsStockTracked).toList();
        for (ProductDto product : stockTrackedProducts) {
            inventoryService.createInventory(vendorShop.getId(), product.getId(), 0);
        }
        deliveryService.createDefaultDeliverySettingsForShop(vendorShop);
    }
}
