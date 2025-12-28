package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ForbiddenException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.CartManagement.dto.CartItemDto;
import com.peters.cafecart.features.CartManagement.dto.base.DeliveryOrderTypeDto;
import com.peters.cafecart.features.CartManagement.dto.base.PickupOrderTypeDto;
import com.peters.cafecart.features.CartManagement.dto.request.CartOptionsDto;
import com.peters.cafecart.features.CartManagement.dto.response.CartAndOrderSummaryDto;
import com.peters.cafecart.features.CartManagement.service.CartServiceImpl;
import com.peters.cafecart.features.InventoryManagement.service.InventoryServiceImpl;
import com.peters.cafecart.features.OrderManagement.entity.Order;
import com.peters.cafecart.features.OrderManagement.service.OrderServiceImpl;
import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
import com.peters.cafecart.shared.enums.OrderTypeEnum;
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
    @Autowired CartServiceImpl cartService;
    @Autowired VendorShopsServiceImpl vendorShopsService;
    @Autowired InventoryServiceImpl inventoryService;
    @Autowired OrderServiceImpl orderService;
    @Autowired ProductServiceImpl productService;
    @Autowired NotificationService notificationService;
    @Autowired IdempotentRequestsService idempotentRequestsService;

    @Transactional
    public void createOrder(Long customerId, CartOptionsDto cartOptionsDto, String idempotencyKey) {

        CartAndOrderSummaryDto cartAndOrderSummaryDto = cartService.getCartAndOrderSummary(customerId, cartOptionsDto);
        Long shopId = cartAndOrderSummaryDto.getCartSummary().getShopId();
        if (vendorShopsService.isCustomerBlockedByShop(shopId, customerId))
            throw new ForbiddenException("You can't make an order to that shop");


        String requestHash = idempotentRequestsService.hashRequest(cartAndOrderSummaryDto);
        Optional<IdempotentRequest> requestCheck = idempotentRequestsService.getIdempotentRequestByUserIdAndIdempotencyKey(customerId, idempotencyKey);
        if (requestCheck.isPresent()) {
            IdempotentRequest request = requestCheck.get();
            if (!request.getRequestHash().equals(requestHash)) throw new ValidationException("Invalid Request");
            return;
        }

        Optional<VendorShop> vendorShop = vendorShopsService.getVendorShop(shopId);
        if (vendorShop.isEmpty())
            throw new ValidationException("Shop is not found");

        if (!vendorShop.get().getIsOnline())
            throw new ValidationException("Shop is not online");

        var orderType = cartAndOrderSummaryDto.getOrderSummary().getOrderTypeBase();
        if (orderType instanceof DeliveryOrderTypeDto delivery) {
            validateDeliveryOrder(cartAndOrderSummaryDto, delivery);
        }


        if (cartAndOrderSummaryDto.getOrderSummary().getPaymentMethod().equals(PaymentMethodEnum.CREDIT_CARD)
                && !vendorShop.get().isOnlinePaymentAvailable())
            throw new ValidationException("Online payment is not available for this shop");

        List<CartItemDto> cartItems = cartAndOrderSummaryDto.getOrderSummary().getItems();
        List<CartItemDto> inventoryTrackedItems = cartItems
                .stream()
                .filter(i -> productService.isStockTracked(i.getProductId()))
                .toList();

        if (!inventoryTrackedItems.isEmpty()) {
            inventoryService.reduceInventoryStockInBulk(customerId, inventoryTrackedItems);
        }

        Order order = orderService.createOrderEntityFromCartAndOrderSummaryDto(cartAndOrderSummaryDto);

        try {
            orderService.saveOrder(order);
            cartService.clearAllCartItems(customerId);
            notificationService.notifyShopOfNewOrder(vendorShop.get().getId().toString());
            idempotentRequestsService.saveRequest(idempotencyKey, customerId, requestHash);
        } catch (Exception e) {
            throw new ValidationException("Failed to save order " + e.getMessage());
        }
    }

    private void validateDeliveryOrder (CartAndOrderSummaryDto cartAndOrderSummaryDto, DeliveryOrderTypeDto orderTypeDto) {
        if (!cartAndOrderSummaryDto.getCartSummary().isDeliveryAvailable()) throw new ValidationException("Delivery Service is not available for this shop");
        if (orderTypeDto.getDeliveryAreaId() == null) throw new ValidationException("Please choose a delivery area");
    }


}


