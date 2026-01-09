package com.peters.cafecart.features.ShopProductManagement.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.peters.cafecart.features.ShopProductManagement.projection.ShopProductAvailabilityView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.ShopProductManagement.projection.ShopProductStock;
import com.peters.cafecart.features.ShopProductManagement.entity.ShopProduct;
import com.peters.cafecart.features.InventoryManagement.entity.Inventory;
import com.peters.cafecart.features.InventoryManagement.service.InventoryServiceImpl;
import com.peters.cafecart.features.ShopProductManagement.dto.ShopProductDto;
import com.peters.cafecart.features.ShopProductManagement.mapper.ShopProductMapper;
import com.peters.cafecart.features.ShopProductManagement.repository.ShopProductRepository;

@Service
public class ShopProductServiceImpl implements ShopProductService {

    @Autowired private ShopProductRepository shopProductRepository;
    @Autowired private ShopProductMapper shopProductMapper;
    @Autowired private InventoryServiceImpl inventoryServiceImpl;
    
    @Override
    public List<ShopProductDto> findAllForVendorShop(long vendorShopId) {
       List<ShopProduct> shopProductStock = shopProductRepository.findAllByVendorShopId(vendorShopId);
       List<ShopProductDto> shopProductDtoList = shopProductMapper.toDtoList(shopProductStock);
       List<Inventory> inventoryList = inventoryServiceImpl.getInventoryByVendorShopId(vendorShopId);
       
       for (ShopProductDto shopProductDto : shopProductDtoList) {
           for (Inventory inventory : inventoryList) {
               if (Objects.equals(shopProductDto.getProductId(), inventory.getProduct().getId()) && Objects.equals(shopProductDto.getVendorShopId(), inventory.getVendorShop().getId())) {
                   shopProductDto.setQuantity(inventory.getQuantity());
               }
           }
       }

       return shopProductDtoList;
    }

    @Override
    public Set<Long> findAllProductIdsForVendorShop(long vendorShopId) {
       return shopProductRepository.findAllProductIdsForVendorShop(vendorShopId);
    }

    @Override
    public List<ShopProductDto> findAllByVendorShopIdAndIsAvailableTrue(long vendorShopId) {
       List<ShopProductStock> shopProductStock = shopProductRepository.findAllByVendorShopIdAndIsAvailableTrue(vendorShopId);
       return shopProductMapper.shopProductStocktoDtoList(shopProductStock);
    }

    @Override
    public ShopProductDto findByProductAndVendorShop(long productId, long vendorShopId) {
        Optional<ShopProductStock> shopProduct = shopProductRepository.findByProductAndVendorShop(productId, vendorShopId);
        return shopProductMapper.shopProductStockToDto(shopProduct.orElseThrow(() -> new ResourceNotFoundException("Product not found")));
    }

    @Override
    public boolean addAProductToAllShops(Long productId, Set<Long> vendorShopIds, boolean isAvailable) {
        shopProductRepository.addProductToVendorShops(productId, vendorShopIds, isAvailable);
        return true;
    }

    @Override
    public boolean addAllProductsToAShop(Long vendorShopId, Set<Long> productIds, boolean isAvailable) {
        shopProductRepository.addProductsToVendorShop(productIds, vendorShopId, isAvailable);
        return true;
    }

    @Override
    public boolean publishShopProduct(long productId, long vendorShopId) {
        Optional<ShopProduct> shopProductCheck = shopProductRepository.findShopProductByProductAndVendorShop(productId, vendorShopId);
        if (shopProductCheck.isEmpty()) throw new ResourceNotFoundException("ShopProduct not found");
        ShopProduct shopProduct = shopProductCheck.get();
        if (shopProduct.isAvailable()) throw new ValidationException("ShopProduct already published");
        shopProduct.setAvailable(true);
        shopProductRepository.save(shopProduct);
        return true;
    }

    @Override
    public boolean unpublishShopProduct(long productId, long vendorShopId) {
        Optional<ShopProduct> shopProductCheck = shopProductRepository.findShopProductByProductAndVendorShop(productId, vendorShopId);
        if (shopProductCheck.isEmpty()) throw new ResourceNotFoundException("ShopProduct not found");
        ShopProduct shopProduct = shopProductCheck.get();
        if (!shopProduct.isAvailable()) throw new ValidationException("ShopProduct already unpublished");
        shopProduct.setAvailable(false);
        shopProductRepository.save(shopProduct);
        return true;
    }

    @Override
    public ShopProductAvailabilityView getShopProductAvailability(Long productId, Long shopId) {
        return shopProductRepository.findStockCheck(productId, shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Information Not Found"));
    }
}
