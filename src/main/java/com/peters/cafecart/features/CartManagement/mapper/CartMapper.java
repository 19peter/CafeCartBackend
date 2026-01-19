package com.peters.cafecart.features.CartManagement.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.peters.cafecart.features.CartManagement.dto.CartItemDto;
import com.peters.cafecart.features.CartManagement.entity.CartItem;

@Mapper(componentModel = "spring")
public interface CartMapper {
    List<CartItemDto> cartItemsToCartItemsDto(List<CartItem> cartItems);
}