package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.CartManagement.dto.request.AddToCartDto;
import com.peters.cafecart.features.CartManagement.entity.Cart;
import com.peters.cafecart.features.CartManagement.entity.CartItem;
import com.peters.cafecart.features.CartManagement.service.CartServiceImpl;
import com.peters.cafecart.features.CustomerManagement.service.CustomerServiceImpl;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.entity.ProductOption;
import com.peters.cafecart.features.ProductsManagement.service.ProductOptionsServiceImpl;
import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import com.peters.cafecart.features.AdditionsManagement.service.AdditionGroupService;
import com.peters.cafecart.features.ShopProductManagement.projection.ShopProductAvailabilityView;
import com.peters.cafecart.features.ShopProductManagement.service.ShopProductServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AddToCartUseCase {
    @Autowired private CustomerServiceImpl customerService;
    @Autowired private VendorShopsServiceImpl vendorShopService;
    @Autowired private CartServiceImpl cartService;
    @Autowired private ShopProductServiceImpl shopProductService;
    @Autowired private EntityManager entityManager;
    @Autowired private ProductOptionsServiceImpl productOptionsService;
    @Autowired private AdditionGroupService additionGroupService;

    public void execute(Long customerId, AddToCartDto addToCartDto) {
        if (customerId == null || addToCartDto.getShopId() == null || addToCartDto.getProductOptionId() == null)
            throw new ValidationException("Customer ID, Shop ID and Product ID cannot be null");

        Long shopId = addToCartDto.getShopId();
        Long productOptionId = addToCartDto.getProductOptionId();

        Cart cart = cartService.getCartForCustomer(customerId);
        if (cart == null) throw new ValidationException("Cart Not Found");
        if (cart.getShop() != null && !cart.getShop().getId().equals(shopId))
            throw new ValidationException("Cart has items from another shop");

        if (cart.getShop() == null)
            cart.setShop(vendorShopService.getVendorShop(shopId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor shop not found")));

        ProductOption productOption = productOptionsService.getProductOption(productOptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));
        Product product = productOption.getProduct();
        ShopProductAvailabilityView shopProduct = shopProductService.getShopProductAvailability(product.getId(), shopId);
        if (!shopProduct.getIsAvailable()) throw new ValidationException("Failed To Add Product: Product is not available");

        List<Long> requestedAdditions = addToCartDto.getAdditionsIds() != null 
                ? addToCartDto.getAdditionsIds() 
                : new ArrayList<>();

        additionGroupService.validateAdditions(product, requestedAdditions);

        Optional<CartItem> optionalCartItem = cart.getItems().stream()
                .filter(item -> item.getProductOption().getId().equals(productOptionId) && 
                               new HashSet<>(item.getAdditionsIds()).containsAll(requestedAdditions) &&
                               new HashSet<>(requestedAdditions).containsAll(item.getAdditionsIds()))
                .findFirst();

        CartItem cartItem;

        if (optionalCartItem.isPresent()) {
            cartItem = optionalCartItem.get();
            if (shopProduct.getIsStockTracked() && cartItem.getQuantity() + addToCartDto.getQuantity() > shopProduct.getQuantity())
                throw new ValidationException("Can't Add Product: Not Enough Stock");
            cartItem.setQuantity(cartItem.getQuantity() + addToCartDto.getQuantity());
        } else {
            ProductOption option = entityManager.getReference(ProductOption.class, productOptionId);
            if (shopProduct.getIsStockTracked() &&  addToCartDto.getQuantity() > shopProduct.getQuantity())
                throw new ValidationException("Can't Add Product: Not Enough Stock");
            cartItem = cartService.createCartItem(addToCartDto, cart, option);
            cart.getItems().add(cartItem);
        }
        cartService.saveCart(cart);
    }
}
