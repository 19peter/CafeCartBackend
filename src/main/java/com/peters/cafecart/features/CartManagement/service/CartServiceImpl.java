package com.peters.cafecart.features.CartManagement.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peters.cafecart.features.CartManagement.dto.AddToCartDto;
import com.peters.cafecart.features.CartManagement.dto.CartAndOrderSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.CartOptionsDto;
import com.peters.cafecart.features.CartManagement.dto.CartSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.OrderSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.RemoveFromCart;
import com.peters.cafecart.features.CartManagement.entity.Cart;
import com.peters.cafecart.features.CartManagement.entity.CartItem;
import com.peters.cafecart.features.CartManagement.mapper.CartMapper;
import com.peters.cafecart.features.CartManagement.repository.CartItemRepository;
import com.peters.cafecart.features.CartManagement.repository.CartRepository;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.CustomerManagement.service.CustomerServiceImpl;
import com.peters.cafecart.features.DeliveryManagment.dto.CustomerLocationRequestDto;
import com.peters.cafecart.features.DeliveryManagment.service.DeliveryServiceImpl;
import com.peters.cafecart.features.InventoryManagement.projections.ShopProductSummary;
import com.peters.cafecart.features.InventoryManagement.service.InventoryServiceImpl;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import com.peters.cafecart.shared.enums.OrderTypeEnum;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;

import jakarta.transaction.Transactional;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;

@Service
public class CartServiceImpl implements CartService {
    @Autowired private CartMapper cartMapper;
    @Autowired private DeliveryServiceImpl deliveryService;
    @Autowired private CustomerServiceImpl customerService;
    @Autowired private VendorShopsServiceImpl vendorShopService;
    @Autowired private ProductServiceImpl productService;
    @Autowired private InventoryServiceImpl inventoryService;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private CartRepository cartRepository;

    @Override
    public void addOneToCart(Long customerId, AddToCartDto addToCartDto) {
        if (customerId == null || addToCartDto.getShopId() == null || addToCartDto.getProductId() == null)
            throw new ValidationException("Customer ID, Shop ID and Product ID cannot be null");
        Customer customer = customerService.getCustomerById(customerId);

        Cart cart = customer.getCart();

        /// Validation 1
        Optional<ShopProductSummary> shopProductSummary = inventoryService
                .getShopProductSummaryByVendorShopIdAndProductId(
                        addToCartDto.getShopId(), addToCartDto.getProductId());

        if (shopProductSummary.isEmpty())
            throw new ResourceNotFoundException("Product not found");

        var shopId = shopProductSummary.get().getVendorShopId();

        if (cart.getShop() == null)
            cart.setShop(vendorShopService.getVendorShop(shopId)
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
            Product product = productService.getProductById(addToCartDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            cartItem = createCartItem(addToCartDto, cart, product);
            cart.getItems().add(cartItem);
        }
        // Cascades saves cartItem
        cartRepository.save(cart);
    }

    @Override
    public void removeOneFromCart(Long customerId, RemoveFromCart removeFromCart) {
        if (cartItemRepository.findCustomerIdByCartItemId(removeFromCart.getCartItemId()) != customerId)
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

        cart = validateCartShop(cart);
        if (cart == null) throw new ValidationException("Cart is empty");
        cartRepository.save(cart);
    }

    @Override
    public CartAndOrderSummaryDto getCartAndOrderSummary(Long customerId, CartOptionsDto cartOptionsDto) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        CartSummaryDto cartSummary = new CartSummaryDto();
        OrderSummaryDto orderSummary = new OrderSummaryDto();
        CartAndOrderSummaryDto cartAndOrderSummaryDto = new CartAndOrderSummaryDto();
        
        if (cart.getShop() != null) {
            cartSummary = createCartSummary(cart, cartOptionsDto);
            orderSummary = createOrderSummary(cart, cartOptionsDto, cart.getShop().getId());
            cartAndOrderSummaryDto.setCartSummary(cartSummary);
            cartAndOrderSummaryDto.setOrderSummary(orderSummary);
        }

        return cartAndOrderSummaryDto;
    }


    @Override
    public void clearAllCartItems(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.getItems().clear();
        cart = validateCartShop(cart);
        if (cart == null) throw new ValidationException("Cart is empty");
        cartRepository.save(cart);
    }

    @Override
    public String getCartShop(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        if (cart.getShop() == null) return "Empty Cart";
        return cart.getShop().getName();
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

    private CartSummaryDto createCartSummary(Cart cart, CartOptionsDto cartOptionsDto) {
        CartSummaryDto cartSummary = new CartSummaryDto();
        cartSummary.setCustomerId(cart.getCustomer().getId());
        cartSummary.setShopId(cart.getShop().getId());
        cartSummary.setVendorId(cart.getShop().getVendor().getId());
        cartSummary.setId(cart.getId());
        cartSummary.setDeliveryAvailable(cart.getShop().isDeliveryAvailable());
        cartSummary.setOnlinePaymentAvailable(cart.getShop().isOnlinePaymentAvailable());
        cartSummary.setOnline(cart.getShop().getIsOnline());
        return cartSummary;
    }

    private OrderSummaryDto createOrderSummary(Cart cart, CartOptionsDto cartOptionsDto, Long shopId) {
        OrderSummaryDto orderSummary = new OrderSummaryDto();
        orderSummary.setOrderType(cartOptionsDto.getOrderType() != null ? cartOptionsDto.getOrderType() : null);
        orderSummary
                .setPaymentMethod(cartOptionsDto.getPaymentMethod() != null ? cartOptionsDto.getPaymentMethod() : null);
        orderSummary.setItems(cartMapper.cartItemsToCartItemsDto(cart.getItems()));
        orderSummary.setShopName(cart.getShop().getName());

        // Calculate sub total
        double subTotal = cart.getItems().stream()
                .mapToDouble(item -> item.getUnitPrice().doubleValue() * item.getQuantity())
                .sum();
        orderSummary.setSubTotal(subTotal);

        // Check if delivery is selected
        if (cartOptionsDto.getOrderType().equals(OrderTypeEnum.DELIVERY)) {
            if (cartOptionsDto.getLatitude() == null || cartOptionsDto.getLongitude() == null)
                throw new ValidationException(
                        "Location coordinates are not provided. Please provide location coordinates.");
            // Calculate delivery fee
            CustomerLocationRequestDto customerLocationRequestDto = new CustomerLocationRequestDto();
            customerLocationRequestDto.setShopId(shopId);
            customerLocationRequestDto.setLatitude(cartOptionsDto.getLatitude());
            customerLocationRequestDto.setLongitude(cartOptionsDto.getLongitude());
            orderSummary.setDeliveryFee(deliveryService.calculateDeliveryCost(customerLocationRequestDto));
            // Subtotal + delivery fee
            subTotal += orderSummary.getDeliveryFee();
        } else {
            orderSummary.setDeliveryFee(0);
        }

        // Check if online payment is selected
        if (cartOptionsDto.getPaymentMethod().equals(PaymentMethodEnum.CREDIT_CARD)) {
            // Calculate transaction fee: subtotal * 2.75% + 3
            double transactionFee = subTotal * 0.0275 + 3;
            // round to 2 decimal places
            transactionFee = Math.round(transactionFee * 100.0) / 100.0;
            orderSummary.setTransactionFee(transactionFee);
            // Subtotal + transaction fee
            subTotal += transactionFee;
        } else {
            orderSummary.setTransactionFee(0);
        }

        // Calculate total
        orderSummary.setTotal(subTotal);

        return orderSummary;
    }

    private Cart validateCartShop(Cart cart) {
        if (cart.getItems().isEmpty())
            cart.setShop(null);
        return cart;
    }

}
