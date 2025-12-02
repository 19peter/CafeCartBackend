package com.peters.cafecart.features.ShopProductManagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.ShopProductManagement.projection.ShopProductStock;
import com.peters.cafecart.features.ShopProductManagement.dto.ShopProductDto;
import com.peters.cafecart.features.ShopProductManagement.mapper.ShopProductMapper;
import com.peters.cafecart.features.ShopProductManagement.repository.ShopProductRepository;

@Service
public class ShopProductServiceImpl implements ShopProductService {

    @Autowired private ShopProductRepository shopProductRepository;
    @Autowired private ShopProductMapper shopProductMapper;
    
    @Override
    public List<ShopProductDto> findAllByVendorShopIdAndIsAvailableTrue(long vendorShopId) {
       List<ShopProductStock> shopProductStock = shopProductRepository.findAllByVendorShopIdAndIsAvailableTrue(vendorShopId);
       return shopProductMapper.shopProductStocktoDtoList(shopProductStock);
    }

    @Override
    public ShopProductDto findByProductAndVendorShop(long productId, long vendorShopId) {
        Optional<ShopProductStock> shopProduct = shopProductRepository.findByProductAndVendorShop(productId, vendorShopId);
        return shopProductMapper.shopProductStocktoDto(shopProduct.orElseThrow(() -> new ResourceNotFoundException("ShopProduct not found")));
    }
}
