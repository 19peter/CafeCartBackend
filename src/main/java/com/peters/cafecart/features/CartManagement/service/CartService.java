package com.peters.cafecart.features.CartManagement.service;

import com.peters.cafecart.features.CartManagement.dto.AddToCartDto;
import com.peters.cafecart.features.CartManagement.dto.RemoveFromCart;

public interface CartService {
    public void addOneToCart(AddToCartDto addToCartDto);

    public void removeOneFromCart(RemoveFromCart removeFromCart);
}
