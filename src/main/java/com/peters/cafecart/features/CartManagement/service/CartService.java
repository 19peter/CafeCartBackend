package com.peters.cafecart.features.CartManagement.service;

import com.peters.cafecart.features.CartManagement.dto.request.RemoveFromCart;
import com.peters.cafecart.features.CartManagement.dto.response.CartAndOrderSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.request.CartOptionsDto;
import com.peters.cafecart.features.CartManagement.entity.Cart;

public interface CartService {

    void removeOneFromCart(Long customerId, RemoveFromCart removeFromCart);

    CartAndOrderSummaryDto getCartAndOrderSummary(Long customerId, CartOptionsDto cartOptionsDto);

    void clearItem(Long cartItemId);

    void clearAllCartItems(Long cartId);

    String getCartShop(Long customerId);

    void saveCart(Cart cart);
}
