//package com.peters.cafecart.workflows;
//
//import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
//import com.peters.cafecart.features.CartManagement.dto.response.CartAndOrderSummaryDto;
//import com.peters.cafecart.features.CartManagement.dto.CartItemDto;
//import com.peters.cafecart.features.CartManagement.dto.request.CartOptionsDto;
//import com.peters.cafecart.features.CartManagement.dto.OrderSummaryDto;
//import com.peters.cafecart.features.CartManagement.dto.CartSummaryDto;
//import com.peters.cafecart.features.CartManagement.service.CartServiceImpl;
//import com.peters.cafecart.features.InventoryManagement.service.InventoryServiceImpl;
//import com.peters.cafecart.features.OrderManagement.entity.Order;
//import com.peters.cafecart.features.OrderManagement.enums.PaymentStatus;
//import com.peters.cafecart.features.OrderManagement.service.OrderServiceImpl;
//import com.peters.cafecart.features.PaymentManagement.Service.PaymentServiceImpl;
//import com.peters.cafecart.features.ProductsManagement.service.ProductServiceImpl;
//import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
//import com.peters.cafecart.features.ShopManagement.service.VendorShopsServiceImpl;
//import com.peters.cafecart.shared.enums.OrderTypeEnum;
//import com.peters.cafecart.shared.enums.PaymentMethodEnum;
//import com.peters.cafecart.shared.services.Idempotency.entity.IdempotentRequest;
//import com.peters.cafecart.shared.services.Idempotency.service.IdempotentRequestsService;
//import com.peters.cafecart.shared.services.WebSockets.NotificationService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CreateOrderUseCaseTest {
//
//    @InjectMocks
//    private CreateOrderUseCase createOrderUseCase;
//
//    @Mock private CartServiceImpl cartService;
//    @Mock private VendorShopsServiceImpl vendorShopsService;
//    @Mock private InventoryServiceImpl inventoryService;
//    @Mock private OrderServiceImpl orderService;
//    @Mock private PaymentServiceImpl paymentService;
//    @Mock private ProductServiceImpl productService;
//    @Mock private NotificationService notificationService;
//    @Mock private IdempotentRequestsService idempotentRequestsService;
//
//    private CartOptionsDto cartOptionsDto;
//    private CartAndOrderSummaryDto summaryDto;
//    private OrderSummaryDto orderSummary;
//    private VendorShop shop;
//    private Order order;
//
//    private final Long CUSTOMER_ID = 1L;
//    private final String IDEMPOTENCY_KEY = "idem-key";
//
//    @BeforeEach
//    void setUp() {
//        cartOptionsDto = new CartOptionsDto();
//        cartOptionsDto.setOrderType(OrderTypeEnum.DELIVERY);
//        cartOptionsDto.setAddress("Cairo");
//
//        orderSummary = new OrderSummaryDto();
//        orderSummary.setOrderType(OrderTypeEnum.DELIVERY);
//        orderSummary.setPaymentMethod(PaymentMethodEnum.CASH);
//        orderSummary.setItems(List.of(cartItem(10L)));
//
//        CartSummaryDto cartSummary = new CartSummaryDto();
//        cartSummary.setShopId(1L);
//
//        summaryDto = new CartAndOrderSummaryDto();
//        summaryDto.setOrderSummary(orderSummary);
//        summaryDto.setCartSummary(cartSummary);
//
//        shop = new VendorShop();
//        shop.setId(1L);
//        shop.setIsOnline(true);
//        shop.setDeliveryAvailable(true);
//        shop.setOnlinePaymentAvailable(true);
//
//        order = new Order();
//    }
//
//
//    /* ---------------- Idempotency ---------------- */
//
//    @Test
//    void shouldReturnWhenIdempotentRequestAlreadyProcessed() {
//        when(cartService.getCartAndOrderSummary(CUSTOMER_ID, cartOptionsDto))
//                .thenReturn(summaryDto);
//
//        when(idempotentRequestsService.hashRequest(summaryDto))
//                .thenReturn("hash");
//
//        IdempotentRequest existing = new IdempotentRequest();
//        existing.setRequestHash("hash");
//
//        when(idempotentRequestsService
//                .getIdempotentRequestByUserIdAndIdempotencyKey(CUSTOMER_ID, IDEMPOTENCY_KEY))
//                .thenReturn(Optional.of(existing));
//
//        createOrderUseCase.createOrder(CUSTOMER_ID, cartOptionsDto, IDEMPOTENCY_KEY);
//
//        verify(orderService, never()).saveOrder(any());
//    }
//
//    @Test
//    void shouldThrowWhenIdempotentHashMismatch() {
//        when(cartService.getCartAndOrderSummary(any(), any())).thenReturn(summaryDto);
//        when(idempotentRequestsService.hashRequest(summaryDto)).thenReturn("new-hash");
//
//        IdempotentRequest existing = new IdempotentRequest();
//        existing.setRequestHash("old-hash");
//
//        when(idempotentRequestsService
//                .getIdempotentRequestByUserIdAndIdempotencyKey(any(), any()))
//                .thenReturn(Optional.of(existing));
//
//        assertThrows(ValidationException.class,
//                () -> createOrderUseCase.createOrder(CUSTOMER_ID, cartOptionsDto, IDEMPOTENCY_KEY));
//    }
//
//    /* ---------------- Shop validations ---------------- */
//
//    @Test
//    void shouldThrowWhenShopNotFound() {
//        mockNoIdempotency();
//
//        when(vendorShopsService.getVendorShop(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ValidationException.class,
//                () -> createOrderUseCase.createOrder(CUSTOMER_ID, cartOptionsDto, IDEMPOTENCY_KEY));
//    }
//
//    @Test
//    void shouldThrowWhenShopOffline() {
//        mockNoIdempotency();
//        shop.setIsOnline(false);
//
//        when(vendorShopsService.getVendorShop(1L)).thenReturn(Optional.of(shop));
//
//        assertThrows(ValidationException.class,
//                () -> createOrderUseCase.createOrder(CUSTOMER_ID, cartOptionsDto, IDEMPOTENCY_KEY));
//    }
//
//    @Test
//    void shouldThrowWhenDeliveryNotAvailable() {
//        mockNoIdempotency();
//        shop.setDeliveryAvailable(false);
//
//        when(vendorShopsService.getVendorShop(1L)).thenReturn(Optional.of(shop));
//
//        assertThrows(ValidationException.class,
//                () -> createOrderUseCase.createOrder(CUSTOMER_ID, cartOptionsDto, IDEMPOTENCY_KEY));
//    }
//
//    /* ---------------- Inventory ---------------- */
//
//    @Test
//    void shouldReduceInventoryForTrackedItems() {
//        mockHappyPath();
//
//        when(productService.isStockTracked(10L)).thenReturn(true);
//        when(orderService.createOrderEntityFromCartAndOrderSummaryDto(summaryDto))
//                .thenReturn(order);
//
//        createOrderUseCase.createOrder(CUSTOMER_ID, cartOptionsDto, IDEMPOTENCY_KEY);
//
//        verify(inventoryService).reduceInventoryStockInBulk(eq(CUSTOMER_ID), anyList());
//    }
//
//    /* ---------------- Payment ---------------- */
//
//    @Test
//    void shouldCreatePaymentIntentionForCreditCard() {
//        mockHappyPath();
//        orderSummary.setPaymentMethod(PaymentMethodEnum.CREDIT_CARD);
//
//        when(orderService.createOrderEntityFromCartAndOrderSummaryDto(summaryDto))
//                .thenReturn(order);
//
//        createOrderUseCase.createOrder(CUSTOMER_ID, cartOptionsDto, IDEMPOTENCY_KEY);
//
//        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
//        verify(paymentService).createIntention(summaryDto);
//    }
//
//    @Test
//    void shouldThrowWhenPaymentIntentionFails() {
//        mockHappyPath();
//        orderSummary.setPaymentMethod(PaymentMethodEnum.CREDIT_CARD);
//
//        when(orderService.createOrderEntityFromCartAndOrderSummaryDto(summaryDto))
//                .thenReturn(order);
//
//        doThrow(new RuntimeException("gateway down"))
//                .when(paymentService).createIntention(any());
//
//        assertThrows(ValidationException.class,
//                () -> createOrderUseCase.createOrder(CUSTOMER_ID, cartOptionsDto, IDEMPOTENCY_KEY));
//    }
//
//    /* ---------------- Success & Failure ---------------- */
//
//    @Test
//    void shouldSaveOrderAndClearCartSuccessfully() {
//        mockHappyPath();
//
//        when(orderService.createOrderEntityFromCartAndOrderSummaryDto(summaryDto))
//                .thenReturn(order);
//
//        createOrderUseCase.createOrder(CUSTOMER_ID, cartOptionsDto, IDEMPOTENCY_KEY);
//
//        verify(orderService).saveOrder(order);
//        verify(cartService).clearAllCartItems(CUSTOMER_ID);
//        verify(notificationService).notifyShopOfNewOrder("1");
//        verify(idempotentRequestsService).saveRequest(any(), any(), any());
//    }
//
//    @Test
//    void shouldThrowWhenSavingOrderFails() {
//        mockHappyPath();
//
//        when(orderService.createOrderEntityFromCartAndOrderSummaryDto(summaryDto))
//                .thenReturn(order);
//
//        doThrow(new RuntimeException("db down"))
//                .when(orderService).saveOrder(any());
//
//        assertThrows(ValidationException.class,
//                () -> createOrderUseCase.createOrder(CUSTOMER_ID, cartOptionsDto, IDEMPOTENCY_KEY));
//    }
//
//    /* ---------------- helpers ---------------- */
//
//    private void mockNoIdempotency() {
//        when(cartService.getCartAndOrderSummary(any(), any()))
//                .thenReturn(summaryDto);
//        when(idempotentRequestsService.hashRequest(summaryDto))
//                .thenReturn("hash");
//        when(idempotentRequestsService
//                .getIdempotentRequestByUserIdAndIdempotencyKey(any(), any()))
//                .thenReturn(Optional.empty());
//    }
//
//    private void mockHappyPath() {
//        mockNoIdempotency();
//        when(vendorShopsService.getVendorShop(1L)).thenReturn(Optional.of(shop));
//        when(productService.isStockTracked(anyLong())).thenReturn(false);
//    }
//
//    private CartItemDto cartItem(Long productId) {
//        CartItemDto dto = new CartItemDto();
//        dto.setProductId(productId);
//        dto.setQuantity(1);
//        return dto;
//    }
//}
