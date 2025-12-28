package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.CartManagement.dto.request.AddToCartDto;
import com.peters.cafecart.features.CartManagement.entity.Cart;
import com.peters.cafecart.features.CartManagement.entity.CartItem;
import com.peters.cafecart.features.CartManagement.service.CartServiceImpl;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.CustomerManagement.service.CustomerServiceImpl;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import com.peters.cafecart.features.ShopProductManagement.dto.ShopProductDto;
import com.peters.cafecart.features.ShopProductManagement.service.ShopProductServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddToCartUseCase {
    @Autowired private CustomerServiceImpl customerService;
    @Autowired private VendorShopsServiceImpl vendorShopService;
    @Autowired private CartServiceImpl cartService;
    @Autowired private ShopProductServiceImpl shopProductService;
    @Autowired private EntityManager entityManager;

    public void execute(Long customerId, AddToCartDto addToCartDto) {
        if (customerId == null || addToCartDto.getShopId() == null || addToCartDto.getProductId() == null)
            throw new ValidationException("Customer ID, Shop ID and Product ID cannot be null");

        Long shopId = addToCartDto.getShopId();
        Long productId = addToCartDto.getProductId();
        ShopProductDto shopProduct = shopProductService.findByProductAndVendorShop(productId, shopId);
        if (!shopProduct.getIsAvailable()) throw new ValidationException("Failed To Add Product: Product is not available");
        if (shopProduct.getIsStockTracked() && shopProduct.getQuantity() < addToCartDto.getQuantity()) throw new ValidationException("Failed To Add Product: Not enough stock");

        Customer customer = customerService.getCustomerById(customerId);
        Cart cart = customer.getCart();

        if (cart.getShop() == null)
            cart.setShop(vendorShopService.getVendorShop(shopId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor shop not found")));

        if (!cart.getShop().getId().equals(shopId))
            throw new ValidationException("Cart has items from another shop");

        Optional<CartItem> optionalCartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        CartItem cartItem;

        if (optionalCartItem.isPresent()) {
            cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + addToCartDto.getQuantity());
        } else {
            Product product = entityManager.getReference(Product.class, productId);
            cartItem = cartService.createCartItem(addToCartDto, cart, product);
            cart.getItems().add(cartItem);
        }
        cartService.saveCart(cart);
    }
}
