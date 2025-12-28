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
import com.peters.cafecart.features.DeliveryManagment.dto.DeliveryAreasDto;
import com.peters.cafecart.features.DeliveryManagment.dto.DeliverySettingsDto;
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
    public CartAndOrderSummaryDto getCartAndOrderSummary(Long customerId, CartOptionsDto cartOptionsDto) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        CartSummaryDto cartSummary;
        OrderSummaryDto orderSummary;
        CartAndOrderSummaryDto cartAndOrderSummaryDto = new CartAndOrderSummaryDto();
        
        if (cart.getShop() != null) {
            Long shopId = cart.getShop().getId();
            DeliverySettingsDto settings = deliveryService.getShopDeliverySettings(shopId);
            cartSummary = createCartSummary(cart, settings.isDeliveryAvailable());
            orderSummary = createOrderSummary(cart, cartOptionsDto, shopId, settings);
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

    public CartItem createCartItem(AddToCartDto addToCartDto, Cart cart, Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(addToCartDto.getQuantity());
        cartItem.setCreatedAt(LocalDateTime.now());
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItem.setUnitPrice(product.getPrice());
        return cartItem;
    }

    private CartSummaryDto createCartSummary(Cart cart, boolean isDeliveryAvailable) {
        CartSummaryDto cartSummary = new CartSummaryDto();
        cartSummary.setCustomerId(cart.getCustomer().getId());
        cartSummary.setShopId(cart.getShop().getId());
        cartSummary.setVendorId(cart.getShop().getVendor().getId());
        cartSummary.setId(cart.getId());
        cartSummary.setDeliveryAvailable(isDeliveryAvailable);
        cartSummary.setOnlinePaymentAvailable(cart.getShop().isOnlinePaymentAvailable());
        cartSummary.setOnline(cart.getShop().getIsOnline());
        return cartSummary;
    }

    private OrderSummaryDto createOrderSummary(Cart cart,
                                               CartOptionsDto cartOptionsDto,
                                               Long shopId,
                                               DeliverySettingsDto settingsDto) {
        OrderSummaryDto orderSummary = new OrderSummaryDto();
        orderSummary
                .setPaymentMethod(cartOptionsDto.getPaymentMethod() != null ? cartOptionsDto.getPaymentMethod() : null);
        orderSummary.setItems(cartMapper.cartItemsToCartItemsDto(cart.getItems()));
        orderSummary.setShopName(cart.getShop().getName());

        // Calculate sub total
        double subTotal = cart.getItems().stream()
                .mapToDouble(item -> item.getUnitPrice().doubleValue() * item.getQuantity())
                .sum();
        orderSummary.setSubTotal(subTotal);

        // Check Order Type
        if (cartOptionsDto.getOrderType().equals(OrderTypeEnum.DELIVERY)) {
            DeliveryOrderTypeDto dto = createDeliveryOrderTypeDto(cartOptionsDto, shopId, settingsDto);
            orderSummary.setOrderTypeBase(dto);
            subTotal += dto.getPrice();
        } else if (cartOptionsDto.getOrderType().equals(OrderTypeEnum.PICKUP))  {
            PickupOrderTypeDto dto = createPickupOrderTypeDto(cartOptionsDto);
            orderSummary.setOrderTypeBase(dto);
        } else {
            InHouseOrderTypeDto dto = createInHouseOrderTypeDto();
            orderSummary.setOrderTypeBase(dto);
        }

        if (cartOptionsDto.getPaymentMethod().equals(PaymentMethodEnum.CREDIT_CARD))
            throw new ResourceNotFoundException("Online Payment Service Not Available");

        // Calculate total
        orderSummary.setTransactionFee(0);

        orderSummary.setTotal(subTotal);

        return orderSummary;
    }

    private DeliveryOrderTypeDto createDeliveryOrderTypeDto(CartOptionsDto cartOptionsDto,
                                                            Long shopId,
                                                            DeliverySettingsDto deliverySettingsDto) {
        DeliveryOrderTypeDto dto = new DeliveryOrderTypeDto();
        dto.setOrderType(OrderTypeEnum.DELIVERY);
        dto.setAddress(cartOptionsDto.getAddress());
        dto.setAvailableDeliveryAreas(deliverySettingsDto.getDeliveryAreasDtoList());

        // Handling only AREA delivery setting
        if (deliverySettingsDto.getDeliverySettingsEnum().equals(DeliverySettingsEnum.DISTANCE)) throw new ResourceNotFoundException("Service Not Available");

        Optional<DeliveryAreasDto> deliveryAreasDtoCheck = deliverySettingsDto.getDeliveryAreasDtoList()
                .stream()
                .filter(a -> Objects.equals(a.getId(), cartOptionsDto.getDeliveryAreaId()))
                .findFirst();


        if (deliveryAreasDtoCheck.isPresent()) {
            var deliveryAreasDto = deliveryAreasDtoCheck.get();
            dto.setDeliveryAreaId(deliveryAreasDto.getId());
            dto.setDeliveryAreaName(deliveryAreasDto.getArea());
            dto.setPrice(deliveryAreasDto.getPrice());
        } else {
            dto.setDeliveryAreaId(null);
            dto.setDeliveryAreaName(null);
            dto.setPrice(0);
        }

        dto.setDeliverySettingsEnum(DeliverySettingsEnum.AREA);
        return dto;
    }

    private PickupOrderTypeDto createPickupOrderTypeDto(CartOptionsDto cartOptionsDto) {
        PickupOrderTypeDto dto = new PickupOrderTypeDto();
        dto.setOrderType(OrderTypeEnum.PICKUP);
        dto.setPickupTime(cartOptionsDto.getPickupTime());
        return dto;
    }

    private InHouseOrderTypeDto createInHouseOrderTypeDto() {
        InHouseOrderTypeDto dto = new InHouseOrderTypeDto();
        dto.setOrderType(OrderTypeEnum.IN_HOUSE);
        return dto;
    }

    private void validateCartShop(Cart cart) {
        if (cart.getItems().isEmpty())
            cart.setShop(null);
    }


}
