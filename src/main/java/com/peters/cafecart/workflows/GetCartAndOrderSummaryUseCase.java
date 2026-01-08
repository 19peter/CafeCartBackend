package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.CartManagement.dto.CartSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.OrderSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.base.DeliveryOrderTypeDto;
import com.peters.cafecart.features.CartManagement.dto.base.InHouseOrderTypeDto;
import com.peters.cafecart.features.CartManagement.dto.base.PickupOrderTypeDto;
import com.peters.cafecart.features.CartManagement.dto.request.CartOptionsDto;
import com.peters.cafecart.features.CartManagement.dto.response.CartAndOrderSummaryDto;
import com.peters.cafecart.features.CartManagement.entity.Cart;
import com.peters.cafecart.features.CartManagement.mapper.CartMapper;
import com.peters.cafecart.features.CartManagement.repository.CartRepository;
import com.peters.cafecart.features.DeliveryManagment.dto.DeliveryAreasDto;
import com.peters.cafecart.features.DeliveryManagment.dto.DeliverySettingsDto;
import com.peters.cafecart.features.DeliveryManagment.service.DeliveryServiceImpl;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.VerifiedCustomerManagement.dto.VerifiedCustomerDto;
import com.peters.cafecart.features.VerifiedCustomerManagement.service.VerifiedCustomerServiceImpl;
import com.peters.cafecart.shared.enums.DeliverySettingsEnum;
import com.peters.cafecart.shared.enums.OrderTypeEnum;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GetCartAndOrderSummaryUseCase {
    @Autowired CartMapper cartMapper;
    @Autowired DeliveryServiceImpl deliveryService;
    @Autowired VerifiedCustomerServiceImpl verifiedCustomerService;
    @Autowired CartRepository cartRepository;

    public CartAndOrderSummaryDto execute(Long customerId, CartOptionsDto cartOptionsDto) {
        //Gets the user Cart
        //From the cart gets yhe shop
        //IF present:
        //1- Get delivery settings for a shop
        //2- Create Cart and Order summaries from the Cart entity

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartSummaryDto cartSummary;
        OrderSummaryDto orderSummary;
        CartAndOrderSummaryDto cartAndOrderSummaryDto = new CartAndOrderSummaryDto();

        if (cart.getShop() != null) {
            VendorShop shop = cart.getShop();
            Long shopId = cart.getShop().getId();
            DeliverySettingsDto settings = deliveryService.getShopDeliverySettings(shopId);
            cartSummary = createCartSummary(cart, settings.isDeliveryAvailable());
            orderSummary = createOrderSummary(cart, cartOptionsDto, shopId, settings);
            VerifiedCustomerDto  verifiedCustomerDto = verifiedCustomerService.isCustomerVerified(
                    cartSummary.getVendorId(),
                    cartSummary.getShopId(),
                    cartSummary.getCustomerId());
            cartSummary.setPhoneNumber(shop.getPhoneNumber());
            cartSummary.setVerified(verifiedCustomerDto.isVerified());
            cartSummary.setAllowedPaymentMethods(generateAllowedPaymentTypes(cartSummary.isVerified()));

            cartAndOrderSummaryDto.setCartSummary(cartSummary);
            cartAndOrderSummaryDto.setOrderSummary(orderSummary);
        }

        return cartAndOrderSummaryDto;
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

    private List<PaymentMethodEnum> generateAllowedPaymentTypes(boolean isVerified) {
        List<PaymentMethodEnum> allowedPayments = new ArrayList<>();
        allowedPayments.add(PaymentMethodEnum.INSTAPAY);
        if (isVerified) allowedPayments.add(PaymentMethodEnum.CASH);
        return allowedPayments;
    }
}
