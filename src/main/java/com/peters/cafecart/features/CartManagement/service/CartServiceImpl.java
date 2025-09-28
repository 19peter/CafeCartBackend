package com.peters.cafecart.features.CartManagement.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.CartManagement.dto.AddToCartDto;
import com.peters.cafecart.features.CartManagement.dto.RemoveFromCart;
import com.peters.cafecart.features.CartManagement.entity.Cart;
import com.peters.cafecart.features.CartManagement.entity.CartItem;
import com.peters.cafecart.features.CartManagement.repository.CartItemRepository;
import com.peters.cafecart.features.CartManagement.repository.CartRepository;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.CustomerManagement.repository.CustomerRepository;
import com.peters.cafecart.features.InventoryManagement.projections.ShopProductSummary;
import com.peters.cafecart.features.InventoryManagement.repository.InventoryRepository;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.repository.ProductRepository;
import com.peters.cafecart.features.VendorManagement.Repository.VendorShopsRepository;
import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private VendorShopsRepository vendorShopRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public void addOneToCart(AddToCartDto addToCartDto) {
        if(addToCartDto.getCustomerId() == null || addToCartDto.getShopId() == null || addToCartDto.getProductId() == null) throw new ValidationException("Customer ID, Shop ID and Product ID cannot be null");
        Customer customer = customerRepository.findById(addToCartDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Cart cart = customer.getCart();

        /// Validation 1
        Optional<ShopProductSummary> shopProductSummary = inventoryRepository
                .findShopProductSummaryByVendorShopIdAndProductId(
                        addToCartDto.getShopId(), addToCartDto.getProductId());
        if (shopProductSummary.isEmpty())
            throw new ResourceNotFoundException("Product not found");

        var shopId = shopProductSummary.get().getVendorShopId();

        if (cart.getShop() == null)
            cart.setShop(vendorShopRepository.findById(shopId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor shop not found")));

        /// Validation 2
        if (!cart.getShop().getId().equals(shopId))
            throw new ValidationException("Cart has items from another shop");

        Optional<CartItem> optionalCartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(addToCartDto.getProductId()))
                .findFirst();

        CartItem cartItem;

        if (optionalCartItem.isPresent()) {
            cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + addToCartDto.getQuantity());
        } else {
            Product product = productRepository.findById(addToCartDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            cartItem = createCartItem(addToCartDto, cart, product);
            cart.getItems().add(cartItem);
        }
        // Cascades saves cartItem
        cartRepository.save(cart);
    }

    @Override
    public void removeOneFromCart(RemoveFromCart removeFromCart) {
        if(removeFromCart.getCartItemId() == null) throw new ValidationException("Cart item ID cannot be null");
        CartItem cartItem = cartItemRepository.findById(removeFromCart.getCartItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartItem.setQuantity(cartItem.getQuantity() - 1);
        if (cartItem.getQuantity() == 0)
            cartItem.getCart().getItems().remove(cartItem);
        if (cartItem.getCart().getItems().isEmpty())
            cartItem.getCart().setShop(null);
        cartRepository.save(cartItem.getCart());
    }

    private CartItem createCartItem(AddToCartDto addToCartDto, Cart cart, Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(addToCartDto.getQuantity());
        cartItem.setCreatedAt(LocalDateTime.now());
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItem.setUnitPrice(product.getPrice());
        return cartItem;
    }

}
