package com.peters.cafecart.features.CartManagement.service;

import com.peters.cafecart.features.CartManagement.dto.AddToCartDto;
import com.peters.cafecart.features.CartManagement.dto.RemoveFromCart;
import com.peters.cafecart.features.CartManagement.dto.CartAndOrderSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.CartOptionsDto;

public interface CartService {
    public void addOneToCart(Long customerId, AddToCartDto addToCartDto);

    public void removeOneFromCart(Long customerId, RemoveFromCart removeFromCart);

    public CartAndOrderSummaryDto getCartAndOrderSummary(Long customerId, CartOptionsDto cartOptionsDto);

    public void clearItem(Long cartItemId);

    public void clearAllCartItems(Long cartId);

    public String getCartShop(Long customerId);
}
