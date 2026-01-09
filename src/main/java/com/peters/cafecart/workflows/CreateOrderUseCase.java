package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ForbiddenException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.CartManagement.dto.CartItemDto;
import com.peters.cafecart.features.CartManagement.dto.base.DeliveryOrderTypeDto;
import com.peters.cafecart.features.CartManagement.dto.request.CartOptionsDto;
import com.peters.cafecart.features.CartManagement.dto.response.CartAndOrderSummaryDto;
import com.peters.cafecart.features.CartManagement.repository.CartRepository;
import com.peters.cafecart.features.CartManagement.service.CartServiceImpl;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.CustomerManagement.service.CustomerServiceImpl;
import com.peters.cafecart.features.InventoryManagement.service.InventoryServiceImpl;
import com.peters.cafecart.features.OrderManagement.dto.OrderResponseDto;
import com.peters.cafecart.features.OrderManagement.entity.Order;
import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;
import com.peters.cafecart.features.OrderManagement.service.OrderServiceImpl;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;
import com.peters.cafecart.shared.services.Idempotency.entity.IdempotentRequest;
import com.peters.cafecart.shared.services.Idempotency.service.IdempotentRequestsService;
import com.peters.cafecart.shared.services.WebSockets.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CreateOrderUseCase {
    @Autowired CustomerServiceImpl customerService;
    @Autowired CartServiceImpl cartService;
    @Autowired CartRepository cartRepository;
    @Autowired VendorShopsServiceImpl vendorShopsService;
    @Autowired InventoryServiceImpl inventoryService;
    @Autowired OrderServiceImpl orderService;
    @Autowired ProductServiceImpl productService;
    @Autowired NotificationService notificationService;
    @Autowired IdempotentRequestsService idempotencyService;
    @Autowired GetCartAndOrderSummaryUseCase cartAndOrderSummaryUseCase;

    @Transactional
    public OrderResponseDto createOrder(Long customerId, CartOptionsDto cartOptionsDto, String idempotencyKey) {
        Customer customer = customerService.getCustomerById(customerId);
        if (customer.getIsEmailVerified() == null || !customer.getIsEmailVerified()) throw new ValidationException("Please verify your email to place an order");

        CartAndOrderSummaryDto cartAndOrderSummaryDto = cartAndOrderSummaryUseCase.execute(customerId, cartOptionsDto);

        String requestHash = idempotencyService.hashRequest(cartAndOrderSummaryDto);

        // STEP 1: Idempotent begin
        IdempotentRequest idem = idempotencyService.begin(customerId, idempotencyKey, requestHash);

        // STEP 2: If already completed, return stored result
        if (idempotencyService.isCompleted(idem)) {
            return idempotencyService.getStoredResponse(idem, OrderResponseDto.class);
        }

        Long shopId = cartAndOrderSummaryDto.getCartSummary().getShopId();
        if (vendorShopsService.isCustomerBlockedByShop(shopId, customerId))
            throw new ForbiddenException("You can't make an order to that shop");

        if (!cartAndOrderSummaryDto.getCartSummary().isVerified())
            if (orderService.doesCustomerHaveAPendingOrder(customerId))
                throw new ValidationException("You can only have one order at a time! Ask the shop to verify you for more!");


        Optional<VendorShop> vendorShop = vendorShopsService.getVendorShop(shopId);
        if (vendorShop.isEmpty())
            throw new ValidationException("Shop is not found");

        if (cartAndOrderSummaryDto.getOrderSummary().getPaymentMethod().equals(PaymentMethodEnum.CREDIT_CARD)
                && !vendorShop.get().isOnlinePaymentAvailable())
            throw new ValidationException("Online payment is not available for this shop");

        if (!vendorShop.get().getIsOnline())
            throw new ValidationException("Shop is not online");

        var orderType = cartAndOrderSummaryDto.getOrderSummary().getOrderTypeBase();
        if (orderType instanceof DeliveryOrderTypeDto delivery) {
            validateDeliveryOrder(cartAndOrderSummaryDto, delivery);
        }


        List<CartItemDto> cartItems = cartAndOrderSummaryDto.getOrderSummary().getItems();
        List<CartItemDto> inventoryTrackedItems = cartItems
                .stream()
                .filter(i -> productService.isStockTracked(i.getProductId()))
                .toList();

        if (!inventoryTrackedItems.isEmpty()) {
            inventoryService.reduceInventoryStockInBulk(shopId, inventoryTrackedItems);
        }

        Order order = orderService.createOrderEntityFromCartAndOrderSummaryDto(cartAndOrderSummaryDto);

        OrderResponseDto response;
        try {
            orderService.saveOrder(order);
            cartService.clearAllCartItems(customerId);
            response = new OrderResponseDto(order.getId(), OrderStatusEnum.PENDING);
            idempotencyService.complete(idem, response);
        } catch (Exception e) {
            idempotencyService.fail(idem, e.getMessage());
            throw new ValidationException("Failed to save order " + e.getMessage());
        }

        try {
            notificationService.notifyShopOfNewOrder(vendorShop.get().getId().toString());
        } catch (Exception notifyEx) {
        }
        return response;

    }

    private void validateDeliveryOrder (CartAndOrderSummaryDto cartAndOrderSummaryDto, DeliveryOrderTypeDto orderTypeDto) {
        if (!cartAndOrderSummaryDto.getCartSummary().isDeliveryAvailable()) throw new ValidationException("Delivery Service is not available for this shop");
        if (orderTypeDto.getDeliveryAreaId() == null) throw new ValidationException("Please choose a delivery area");
    }


}


