package com.peters.cafecart.features.CartManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

import com.peters.cafecart.features.CartManagement.dto.request.AddToCartDto;
import com.peters.cafecart.features.CartManagement.dto.request.CartOptionsDto;
import com.peters.cafecart.features.CartManagement.dto.request.RemoveFromCart;
import com.peters.cafecart.features.CartManagement.dto.response.CartAndOrderSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.peters.cafecart.features.CartManagement.entity.Cart;
import com.peters.cafecart.features.CartManagement.entity.CartItem;
import com.peters.cafecart.features.CartManagement.mapper.CartMapper;
import com.peters.cafecart.features.CartManagement.repository.CartItemRepository;
import com.peters.cafecart.features.CartManagement.repository.CartRepository;
import com.peters.cafecart.features.DeliveryManagment.service.DeliveryServiceImpl;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.shared.enums.OrderTypeEnum;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
    @Mock CartMapper cartMapper;
    @Mock DeliveryServiceImpl deliveryService;
    @Mock CartItemRepository cartItemRepository;
    @Mock CartRepository cartRepository;
    @InjectMocks CartServiceImpl cartService;

    Cart cart;
    CartItem cartItem;
    Product product;
    final Long CUSTOMER_ID = 1L;
    final Long CART_ITEM_ID = 1L;

    @BeforeEach
    void setUp() {
        product = mock(Product.class);
        cartItem = new CartItem();
        cartItem.setId(CART_ITEM_ID);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(BigDecimal.TEN);
        cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>(List.of(cartItem)));
        cartItem.setCart(cart);
    }

    @Test
    void removeOneFromCart_valid_removesOne() {
        when(cartItemRepository.findCustomerIdByCartItemId(CART_ITEM_ID)).thenReturn(CUSTOMER_ID);
        when(cartItemRepository.findById(CART_ITEM_ID)).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any())).thenReturn(cart);
        RemoveFromCart removeFromCart = new RemoveFromCart();
        removeFromCart.setCartItemId(CART_ITEM_ID);
        cartService.removeOneFromCart(CUSTOMER_ID, removeFromCart);
        assertEquals(1, cartItem.getQuantity());
        verify(cartRepository).save(cart);
    }

    @Test
    void removeOneFromCart_lastItem_removesItem() {
        cartItem.setQuantity(1);
        when(cartItemRepository.findCustomerIdByCartItemId(CART_ITEM_ID)).thenReturn(CUSTOMER_ID);
        when(cartItemRepository.findById(CART_ITEM_ID)).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any())).thenReturn(cart);
        RemoveFromCart removeFromCart = new RemoveFromCart();
        removeFromCart.setCartItemId(CART_ITEM_ID);
        cartService.removeOneFromCart(CUSTOMER_ID, removeFromCart);
        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void removeOneFromCart_wrongCustomer_throws() {
        when(cartItemRepository.findCustomerIdByCartItemId(CART_ITEM_ID)).thenReturn(999L);
        RemoveFromCart removeFromCart = new RemoveFromCart();
        removeFromCart.setCartItemId(CART_ITEM_ID);
        assertThrows(ValidationException.class, () -> cartService.removeOneFromCart(CUSTOMER_ID, removeFromCart));
    }

    @Test
    void clearItem_valid_removesItem() {
        when(cartItemRepository.findCartByCartItemId(CART_ITEM_ID)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(CART_ITEM_ID)).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any())).thenReturn(cart);
        cartService.clearItem(CART_ITEM_ID);
        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void getCartAndOrderSummary_valid_returnsSummary() {
        CartOptionsDto options = new CartOptionsDto();
        options.setOrderType(OrderTypeEnum.DELIVERY);
        options.setPaymentMethod(PaymentMethodEnum.CREDIT_CARD);
        options.setLatitude("1.0");
        options.setLongitude("2.0");
        when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.of(cart));
        when(deliveryService.calculateDeliveryCost(any())).thenReturn(5.0);
        when(cartMapper.cartItemsToCartItemsDto(anyList())).thenReturn(Collections.emptyList());
        CartAndOrderSummaryDto result = cartService.getCartAndOrderSummary(CUSTOMER_ID, options);
        assertNotNull(result);
        assertNotNull(result.getOrderSummary());
    }

    @Test
    void clearAllCartItems_clearsItems() {
        when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart);
        cartService.clearAllCartItems(CUSTOMER_ID);
        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    void getCartShop_withShop_returnsName() {
        com.peters.cafecart.features.ShopManagement.entity.VendorShop vendorShop = mock(com.peters.cafecart.features.ShopManagement.entity.VendorShop.class);
        cart.setShop(vendorShop);
        when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.of(cart));
        when(vendorShop.getName()).thenReturn("Test Shop");
        String result = cartService.getCartShop(CUSTOMER_ID);
        assertEquals("Test Shop", result);
    }

    @Test
    void getCartShop_empty_returnsEmptyCart() {
        when(cartRepository.findByCustomerId(CUSTOMER_ID)).thenReturn(Optional.of(cart));
        cart.setShop(null);
        String result = cartService.getCartShop(CUSTOMER_ID);
        assertEquals("Empty Cart", result);
    }

    @Test
    void saveCart_callsRepository() {
        cartService.saveCart(cart);
        verify(cartRepository).save(cart);
    }

    @Test
    void createCartItem_createsItem() {
        AddToCartDto dto = mock(AddToCartDto.class);
        when(dto.getQuantity()).thenReturn(2);
        when(product.getPrice()).thenReturn(BigDecimal.TEN);
        CartItem result = cartService.createCartItem(dto, cart, product);
        assertNotNull(result);
        assertEquals(cart, result.getCart());
        assertEquals(product, result.getProduct());
        assertEquals(2, result.getQuantity());
        assertEquals(BigDecimal.TEN, result.getUnitPrice());
    }
}
