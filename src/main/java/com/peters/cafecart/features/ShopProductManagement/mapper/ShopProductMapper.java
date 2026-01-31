package com.peters.cafecart.features.ShopProductManagement.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.peters.cafecart.features.ShopProductManagement.dto.ShopProductDto;
import com.peters.cafecart.features.ShopProductManagement.entity.ShopProduct;
import com.peters.cafecart.features.ShopProductManagement.projection.ShopProductStock;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShopProductMapper {

    default ShopProductDto toDto(ShopProduct shopProduct) {
        ShopProductDto shopProductDto = new ShopProductDto();
        shopProductDto.setId(shopProduct.getId());
        shopProductDto.setProductId(shopProduct.getProduct().getId());
        shopProductDto.setVendorShopId(shopProduct.getVendorShop().getId());
        shopProductDto.setIsAvailable(shopProduct.isAvailable());
        shopProductDto.setName(shopProduct.getProduct().getName());
        shopProductDto.setImageUrl(shopProduct.getProduct().getImageUrl());
        shopProductDto.setCategoryName(shopProduct.getProduct().getCategory().getName());
        shopProductDto.setIsStockTracked(shopProduct.getProduct().getIsStockTracked());
        
        shopProductDto.setCategoryId(shopProduct.getProduct().getCategory().getId());
        return shopProductDto;
    }
    
    default List<ShopProductDto> toDtoList(List<ShopProduct> shopProducts) {
        return shopProducts.stream().map(this::toDto).collect(Collectors.toList());
    }

    default ShopProductDto shopProductStockToDto(ShopProductStock shopProductStock) {
        ShopProductDto shopProductDto = new ShopProductDto();
        shopProductDto.setId(shopProductStock.getId());
        shopProductDto.setProductId(shopProductStock.getProductId());
        shopProductDto.setVendorShopId(shopProductStock.getVendorShopId());
        shopProductDto.setIsAvailable(shopProductStock.getIsAvailable());
        shopProductDto.setName(shopProductStock.getName());
        shopProductDto.setQuantity(shopProductStock.getQuantity());
        shopProductDto.setImageUrl(shopProductStock.getImageUrl());
        shopProductDto.setCategoryId(shopProductStock.getCategoryId());
        shopProductDto.setCategoryName(shopProductStock.getCategoryName());
        shopProductDto.setIsStockTracked(shopProductStock.getIsStockTracked());
        shopProductDto.setDescription(shopProductStock.getDescription());
        shopProductDto.setIsShopActive(shopProductStock.getIsShopActive());
        shopProductDto.setIsVendorActive(shopProductStock.getIsVendorActive());
        return shopProductDto;
    }

    default List<ShopProductDto> shopProductStocktoDtoList(List<ShopProductStock> shopProductStocks) {
        return shopProductStocks.stream().map(shopProductStock -> {
            if (shopProductStock.getIsShopActive()) {
                return shopProductStockToDto(shopProductStock);
            }
            return null;
        }).collect(Collectors.toList());
    }
}
