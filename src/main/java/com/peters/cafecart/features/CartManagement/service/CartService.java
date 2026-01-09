package com.peters.cafecart.features.CartManagement.service;

import com.peters.cafecart.features.CartManagement.dto.request.RemoveFromCart;
import com.peters.cafecart.features.CartManagement.dto.response.CartAndOrderSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.request.CartOptionsDto;
import com.peters.cafecart.features.CartManagement.entity.Cart;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;

public interface CartService {

    void removeOneFromCart(Long customerId, RemoveFromCart removeFromCart);

    void clearItem(Long cartItemId);

    void clearAllCartItems(Long cartId);

    String getCartShop(Long customerId);

    void saveCart(Cart cart);

    void createCartForNewCustomer(Customer customer);

    Cart getCartForCustomer(Long customerId);
}
