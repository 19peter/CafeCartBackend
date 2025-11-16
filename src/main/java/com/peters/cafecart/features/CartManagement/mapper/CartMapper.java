package com.peters.cafecart.features.CartManagement.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.peters.cafecart.features.CartManagement.dto.CartItemDto;
import com.peters.cafecart.features.CartManagement.entity.CartItem;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "cartId", source = "cart.id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImage", source = "product.imageUrl")
    CartItemDto cartItemToCartItemDto(CartItem cartItem);

    List<CartItemDto> cartItemsToCartItemsDto(List<CartItem> cartItems);

}