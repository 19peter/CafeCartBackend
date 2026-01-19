package com.peters.cafecart.features.CartManagement.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import com.peters.cafecart.features.CartManagement.dto.*;
import com.peters.cafecart.features.CartManagement.dto.base.DeliveryOrderTypeDto;
import com.peters.cafecart.features.CartManagement.dto.base.InHouseOrderTypeDto;
import com.peters.cafecart.features.CartManagement.dto.base.PickupOrderTypeDto;
import com.peters.cafecart.features.CartManagement.dto.request.AddToCartDto;
import com.peters.cafecart.features.CartManagement.dto.request.CartOptionsDto;
import com.peters.cafecart.features.CartManagement.dto.request.RemoveFromCart;
import com.peters.cafecart.features.CartManagement.dto.response.CartAndOrderSummaryDto;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.DeliveryManagment.dto.DeliveryAreasDto;
import com.peters.cafecart.features.DeliveryManagment.dto.DeliverySettingsDto;
import com.peters.cafecart.features.ProductsManagement.entity.ProductOption;
import com.peters.cafecart.shared.enums.DeliverySettingsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.CartManagement.entity.Cart;
import com.peters.cafecart.features.CartManagement.entity.CartItem;
import com.peters.cafecart.features.CartManagement.mapper.CartMapper;
import com.peters.cafecart.features.CartManagement.repository.CartItemRepository;
import com.peters.cafecart.features.CartManagement.repository.CartRepository;
import com.peters.cafecart.features.DeliveryManagment.service.DeliveryServiceImpl;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.shared.enums.OrderTypeEnum;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;

import jakarta.transaction.Transactional;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;

@Service
public class CartServiceImpl implements CartService {
    @Autowired private CartMapper cartMapper;
    @Autowired private DeliveryServiceImpl deliveryService;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private CartRepository cartRepository;


    @Override
    public void removeOneFromCart(Long customerId, RemoveFromCart removeFromCart) {
        if (!Objects.equals(cartItemRepository.findCustomerIdByCartItemId(removeFromCart.getCartItemId()), customerId))
            throw new ValidationException("Cart item does not belong to this customer");
        
        Long cartItemId = removeFromCart.getCartItemId();
        if (cartItemId == null)
            throw new ValidationException("Cart item ID cannot be null");

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cartItem.setQuantity(cartItem.getQuantity() - 1);
        if (cartItem.getQuantity() == 0) {
            cartItem.getCart().getItems().remove(cartItem);
            validateCartShop(cartItem.getCart());
        }
        Cart cart = cartItem.getCart();
        if (cart == null) throw new ResourceNotFoundException("Cart not found");
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearItem(Long cartItemId) {
        if (cartItemId == null)
            throw new ValidationException("Cart item ID cannot be null");
        Cart cart = cartItemRepository.findCartByCartItemId(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().remove(cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found")));

        validateCartShop(cart);
        cartRepository.save(cart);
    }
    @Override
    public void clearAllCartItems(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.getItems().clear();
        validateCartShop(cart);
        cartRepository.save(cart);
    }

    @Override
    public String getCartShop(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        if (cart.getShop() == null) return "Empty Cart";
        return cart.getShop().getName();
    }

    @Override
    public void saveCart(Cart cart) {
        cartRepository.save(cart);
    }

    @Override
    public void createCartForNewCustomer(Customer customer){
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setCreatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public Cart getCartForCustomer(Long customerId) {
        if (customerId == null) throw new ResourceNotFoundException("Customer not found");
        Optional<Cart> optionalCart = cartRepository.findByCustomerIdWithItemsAndShop(customerId);
        return optionalCart.orElse(null);
    }

    public CartItem createCartItem(AddToCartDto addToCartDto, Cart cart, ProductOption product) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProductOption(product);
        cartItem.setQuantity(addToCartDto.getQuantity());
        cartItem.setCreatedAt(LocalDateTime.now());
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItem.setUnitPrice(product.getPrice());
        return cartItem;
    }

    private void validateCartShop(Cart cart) {
        if (cart.getItems().isEmpty())
            cart.setShop(null);
    }

}
