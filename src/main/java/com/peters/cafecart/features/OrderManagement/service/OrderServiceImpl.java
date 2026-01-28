package com.peters.cafecart.features.OrderManagement.service;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.CartManagement.dto.*;
import com.peters.cafecart.features.CartManagement.dto.base.DeliveryOrderTypeDto;
import com.peters.cafecart.features.CartManagement.dto.base.InHouseOrderTypeDto;
import com.peters.cafecart.features.CartManagement.dto.base.OrderTypeBase;
import com.peters.cafecart.features.CartManagement.dto.base.PickupOrderTypeDto;
import com.peters.cafecart.features.CartManagement.dto.response.CartAndOrderSummaryDto;
import com.peters.cafecart.features.OrderManagement.dto.*;
import com.peters.cafecart.features.OrderManagement.projections.SalesSummary;
import com.peters.cafecart.features.ProductsManagement.entity.ProductOption;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.AdditionsManagement.entity.Addition;
import com.peters.cafecart.features.OrderManagement.entity.Order;
import com.peters.cafecart.features.OrderManagement.entity.OrderItem;
import com.peters.cafecart.features.OrderManagement.entity.OrderItemAddition;
import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;
import com.peters.cafecart.features.OrderManagement.enums.PaymentStatus;
import com.peters.cafecart.features.OrderManagement.projections.OrderStatusSummary;
import com.peters.cafecart.features.OrderManagement.repository.OrderItemsRepository;
import com.peters.cafecart.features.OrderManagement.repository.OrderRepository;
import com.peters.cafecart.shared.enums.OrderTypeEnum;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.OrderManagement.mapper.OrderMapper;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemsRepository orderItemsRepository;
    @PersistenceContext private EntityManager entityManager;

    // --- PUBLIC SERVICE METHODS ---

    @Override
    public List<ShopOrderDto> getAllOrdersForShop(Long shopId, LocalDate date) {
        LocalDate searchDate = (date != null) ? date : LocalDate.now();
        LocalDateTime startOfDay = searchDate.atStartOfDay();
        LocalDateTime endOfDay = searchDate.plusDays(1).atStartOfDay();

        List<Order> orders = orderRepository.findShopOrdersByDate(shopId, startOfDay, endOfDay);
        return mapToShopOrderDto(orders);
    }

    @Override
    public List<OrderItemDto> getOrderItems(Long shopId, Long orderId) {
        Long orderShopId = orderRepository.findOrderShopIdById(orderId);
        if (orderShopId == null || !orderShopId.equals(shopId)) {
            throw new ValidationException("Order belongs to a different shop");
        }
        return orderItemsRepository.findByOrderId(orderId).stream()
                .map(this::toOrderItemDto)
                .toList();
    }

    @Override
    public List<OrderDto> getAllOrdersForCustomer(Long customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::toOrderDto)
                .toList();
    }

    @Override
    public Page<OrderDto> getAllOrdersByMonth(int shopId, int year, int month, Pageable pageable) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay();

        Page<Order> orderPage = orderRepository.findOrdersByMonth(startOfMonth, endOfMonth, pageable, shopId);
        return orderPage.map(this::toOrderDto);
    }

    @Override
    public OrderDto getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::toOrderDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public void cancelOrder(Long shopId, OrderUpdateDto order) {
        log.info("Cancelling order {} for shop {}", order.getOrderId(), shopId);
        updateOrderStatus(order.getOrderId(), shopId, OrderStatusEnum.CANCELLED);
    }

    @Override
    @Transactional
    public OrderStatusEnum updateOrderStatusToNextState(Long shopId, OrderUpdateDto order) {
        OrderStatusSummary orderProjection = orderRepository.findOrderStatusSummaryById(order.getOrderId());
        if (!orderProjection.getVendorShopId().equals(shopId)) {
            throw new ValidationException("Order belongs to a different shop");
        }

        OrderStatusEnum nextStatus = computeNextStatus(orderProjection.getStatus(), orderProjection.getOrderType());
        if (nextStatus == null) {
            throw new ValidationException("Order status cannot be updated further");
        }

        updateOrderStatus(order.getOrderId(), shopId, nextStatus);
        return nextStatus;
    }

    @Override
    public OrdersTotalPerMonthDto getSalesSummaryForMonthForShop(Long shopId, int year, int month) {
        if (shopId == null || year == 0 || month == 0) throw new ValidationException("Invalid Parameters");

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay();

        SalesSummary sales = orderRepository.getTotalOrdersAndSalesByMonthForShop(startOfMonth, endOfMonth, shopId, OrderStatusEnum.DELIVERED);
        return new OrdersTotalPerMonthDto(sales.getCount(), sales.getTotal());
    }

    @Override
    @Transactional
    public PaymentStatusUpdate updateOrderPaymentStatus(PaymentStatusUpdate update) {
        if (update.getOrderId() == null) throw new ValidationException("Order Id can't be null");
        Order order = orderRepository.findById(update.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));
        order.setPaymentStatus(update.getPaymentStatus());
        return update;
    }

    @Override
    public boolean doesCustomerHaveAPendingOrder(Long customerId) {
        return orderRepository.existsByCustomerIdAndStatus(customerId, OrderStatusEnum.PENDING);
    }

    // --- ORDER CREATION & PERSISTENCE ---

    @Override
    @Transactional
    public void saveOrder(Order order) {
        try {
            if (order == null) throw new ValidationException("Order cannot be null");
            log.debug("Saving order entity: {}", order.getOrderNumber());
            // Persists Order, and via CascadeType.ALL, its Items and their Additions
            orderRepository.save(order);
        } catch (Exception e) {
            log.error("Failed to save order: {}", e.getMessage());
            throw new ValidationException("Could not complete order: " + e.getMessage());
        }
    }

    public Order createOrderEntityFromCartAndOrderSummaryDto(CartAndOrderSummaryDto dto, List<Addition> availableAdditions) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatusEnum.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentMethod(dto.getOrderSummary().getPaymentMethod());

        // Map Customer and Shop references
        CartSummaryDto cartSummary = dto.getCartSummary();
        order.setCustomer(entityManager.getReference(Customer.class, cartSummary.getCustomerId()));
        order.setVendorShop(entityManager.getReference(VendorShop.class, cartSummary.getShopId()));

        // Map Order Items and Additions
        OrderSummaryDto orderSummary = dto.getOrderSummary();
        List<OrderItem> items = orderSummary.getItems().stream()
                .map(itemDto -> createOrderItem(itemDto, order, availableAdditions))
                .toList();
        order.getItems().addAll(items);

        // Map Order Type and Fees
        OrderTypeBase typeBase = orderSummary.getOrderTypeBase();
        order.setOrderType(typeBase.getOrderType());
        if (typeBase instanceof DeliveryOrderTypeDto deliveryDto) {
            order.setDeliveryAddress(deliveryDto.getAddress());
            order.setDeliveryFee(BigDecimal.valueOf(deliveryDto.getPrice()));
        } else if (typeBase instanceof PickupOrderTypeDto pickupDto) {
            order.setPickupTime(pickupDto.getPickupTime());
            order.setDeliveryFee(BigDecimal.ZERO);
        } else {
            order.setDeliveryFee(BigDecimal.ZERO);
        }

        order.setTotalAmount(BigDecimal.valueOf(orderSummary.getSubTotal()));
        return order;
    }

    private OrderItem createOrderItem(CartItemDto itemDto, Order order, List<Addition> availableAdditions) {
        OrderItem item = new OrderItem();
        ProductOption option = entityManager.getReference(ProductOption.class, itemDto.getProductOptionId());

        item.setOrder(order);
        item.setProductOption(option);
        item.setQuantity(itemDto.getQuantity());
        item.setUnitPrice(option.getPrice());
        item.setCreatedAt(LocalDateTime.now());

        // Handle Additions - will be persisted via cascade when Order is saved
        if (itemDto.getAdditions() != null) {

            Map<Long, Addition> additionsById =
                    availableAdditions.stream()
                            .collect(Collectors.toMap(Addition::getId, Function.identity()));

            itemDto.getAdditions().forEach(additionDto -> {
                Addition addition = additionsById.get(additionDto.getId());
                if (addition != null) {
                    item.getAdditions().add(
                            createOrderItemAddition(addition, item)
                    );
                }
            });
        }


        // Recalculate total price for transient field logic (if needed before persistence)
        BigDecimal addsPrice = item.getAdditions().stream()
                .map(OrderItemAddition::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        item.setTotalPrice(item.getUnitPrice().add(addsPrice).multiply(BigDecimal.valueOf(item.getQuantity())));

        return item;
    }

    private OrderItemAddition createOrderItemAddition(Addition addition, OrderItem item) {
        OrderItemAddition oia = new OrderItemAddition();
        oia.setOrderItem(item);
        oia.setAdditionId(addition.getId());
        oia.setName(addition.getName());
        oia.setPrice(addition.getPrice());
        return oia;
    }

    // --- MANUAL ENTITY-TO-DTO MAPPING ---

    private OrderDto toOrderDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setOrderType(order.getOrderType());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalAmount()); // Entity totalAmount is subtotal + fee
        dto.setCreatedAt(order.getCreatedAt());
        dto.setItems(order.getItems().stream().map(this::toOrderItemDto).toList());
        return dto;
    }

    private ShopOrderDto toShopOrderDto(Order order) {
        return new ShopOrderDto(
                order.getId(),
                order.getOrderNumber(),
                order.getOrderType(),
                order.getPaymentMethod(),
                order.getStatus(),
                order.getItems().stream().map(this::toOrderItemDto).toList(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getCustomer().getId(),
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                order.getCustomer().getPhoneNumber(),
                order.getDeliveryAddress(),
                order.getLatitude(),
                order.getLongitude(),
                false // Verified status populated in controller usually
        );
    }

    private List<ShopOrderDto> mapToShopOrderDto(List<Order> orders) {
        return orders.stream().map(this::toShopOrderDto).toList();
    }

    private OrderItemDto toOrderItemDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(item.getId());
        dto.setName(item.getProductOption().getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getUnitPrice());

        if (item.getAdditions() != null) {
            dto.setAdditions(item.getAdditions().stream()
                    .map(this::toOrderItemAdditionDto)
                    .toList());
        }
        return dto;
    }

    private OrderItemAdditionDto toOrderItemAdditionDto(OrderItemAddition oia) {
        OrderItemAdditionDto dto = new OrderItemAdditionDto();
        dto.setId(oia.getId());
        dto.setAdditionId(oia.getAdditionId());
        dto.setName(oia.getName());
        dto.setPrice(oia.getPrice());
        return dto;
    }

    // --- HELPERS ---

    private void updateOrderStatus(Long orderId, Long shopId, OrderStatusEnum status) {
        int updated = orderRepository.updateOrderStatus(orderId, shopId, status);
        if (updated == 0) throw new ValidationException("Failed to update status: Order not found or unauthorized");
    }

    private OrderStatusEnum computeNextStatus(OrderStatusEnum current, OrderTypeEnum type) {
        if (current == null || type == null) return null;
        return switch (current) {
            case PENDING -> OrderStatusEnum.PREPARING;
            case PREPARING -> (type == OrderTypeEnum.DELIVERY) ? OrderStatusEnum.OUT_FOR_DELIVERY : OrderStatusEnum.READY_FOR_PICKUP;
            case READY_FOR_PICKUP, OUT_FOR_DELIVERY -> OrderStatusEnum.DELIVERED;
            default -> null;
        };
    }

    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        StringBuilder randomPart = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            randomPart.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return "ORD-" + datePart + "-" + randomPart;
    }
}
