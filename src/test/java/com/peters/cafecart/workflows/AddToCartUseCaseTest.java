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
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import com.peters.cafecart.features.ShopProductManagement.dto.ShopProductDto;
import com.peters.cafecart.features.ShopProductManagement.service.ShopProductServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddToCartUseCaseTest {

    @InjectMocks
    private AddToCartUseCase addToCartUseCase;

    @Mock private CustomerServiceImpl customerService;
    @Mock private VendorShopsServiceImpl vendorShopService;
    @Mock private CartServiceImpl cartService;
    @Mock private ShopProductServiceImpl shopProductService;
    @Mock private EntityManager entityManager;

    private Customer customer;
    private Cart cart;
    private VendorShop shop;
    private Product product;
    private AddToCartDto dto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        cart = new Cart();
        cart.setItems(new ArrayList<>());
        customer.setCart(cart);

        shop = new VendorShop();
        shop.setId(1L);

        product = new Product();
        product.setId(10L);

        dto = new AddToCartDto();
        dto.setShopId(1L);
        dto.setProductId(10L);
        dto.setQuantity(2);
    }

    @Test
    void shouldThrowWhenAnyRequiredIdIsNull() {
        assertThrows(ValidationException.class,
                () -> addToCartUseCase.execute(null, dto));

        dto.setShopId(null);
        assertThrows(ValidationException.class,
                () -> addToCartUseCase.execute(1L, dto));
    }

    @Test
    void shouldThrowWhenProductIsNotAvailable() {
        ShopProductDto shopProduct = mock(ShopProductDto.class);
        when(shopProduct.getIsAvailable()).thenReturn(false);

        when(shopProductService.findByProductAndVendorShop(10L, 1L))
                .thenReturn(shopProduct);

        assertThrows(ValidationException.class,
                () -> addToCartUseCase.execute(1L, dto));
    }

    @Test
    void shouldThrowWhenNotEnoughStock() {
        ShopProductDto shopProduct = mock(ShopProductDto.class);
        when(shopProduct.getIsAvailable()).thenReturn(true);
        when(shopProduct.getIsStockTracked()).thenReturn(true);
        when(shopProduct.getQuantity()).thenReturn(1);

        when(shopProductService.findByProductAndVendorShop(10L, 1L))
                .thenReturn(shopProduct);

        assertThrows(ValidationException.class,
                () -> addToCartUseCase.execute(1L, dto));
    }

    @Test
    void shouldThrowWhenCartHasDifferentShop() {
        cart.setShop(new VendorShop());
        cart.getShop().setId(99L);

        mockHappyProduct();
        when(customerService.getCustomerById(1L)).thenReturn(customer);

        assertThrows(ValidationException.class,
                () -> addToCartUseCase.execute(1L, dto));
    }

    @Test
    void shouldAddNewItemToCart() {
        mockHappyProduct();

        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(vendorShopService.getVendorShop(1L)).thenReturn(Optional.of(shop));
        when(entityManager.getReference(Product.class, 10L)).thenReturn(product);

        CartItem newItem = new CartItem();
        when(cartService.createCartItem(dto, cart, product)).thenReturn(newItem);

        addToCartUseCase.execute(1L, dto);

        assertEquals(1, cart.getItems().size());
        verify(cartService).saveCart(cart);
    }

    @Test
    void shouldIncreaseQuantityWhenItemAlreadyExists() {
        mockHappyProduct();

        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(1);
        cart.getItems().add(existingItem);

        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(vendorShopService.getVendorShop(1L)).thenReturn(Optional.of(shop));

        addToCartUseCase.execute(1L, dto);

        assertEquals(3, existingItem.getQuantity());
        verify(cartService).saveCart(cart);
    }

    @Test
    void shouldThrowWhenVendorShopNotFound() {
        mockHappyProduct();

        when(customerService.getCustomerById(1L)).thenReturn(customer);
        when(vendorShopService.getVendorShop(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> addToCartUseCase.execute(1L, dto));
    }

    /* ---------- helpers ---------- */

    private void mockHappyProduct() {
        ShopProductDto shopProduct = mock(ShopProductDto.class);
        when(shopProduct.getIsAvailable()).thenReturn(true);
        when(shopProduct.getIsStockTracked()).thenReturn(false);

        when(shopProductService.findByProductAndVendorShop(10L, 1L))
                .thenReturn(shopProduct);
    }
}
