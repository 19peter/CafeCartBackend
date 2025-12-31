package com.peters.cafecart.features.OrderManagement.service;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;
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
import com.peters.cafecart.shared.enums.PaymentMethodEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.OrderManagement.entity.Order;
import com.peters.cafecart.features.OrderManagement.entity.OrderItem;
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
public class OrderServiceImpl implements OrderService {
    SecureRandom RANDOM = new SecureRandom();
    @Autowired OrderRepository orderRepository;
    @Autowired OrderItemsRepository orderItemsRepository;
    @Autowired OrderMapper orderMapper;
    @PersistenceContext EntityManager entityManager;

    @Override
    public List<ShopOrderDto> getAllOrdersForShop(Long shopId, LocalDate date) {
        if (date == null)
            date = LocalDate.now();

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Order> orders = orderRepository.findShopOrdersByDate(shopId, startOfDay, endOfDay);

        return createShopOrderDtoFromOrder(orders);
    }

    @Override
    public List<OrderItemDto> getOrderItems(Long shopId, Long orderId) {
        // First check that the order belongs to the shop
        Long orderShopId = orderRepository.findOrderShopIdById(orderId);
        if (orderShopId == null || !orderShopId.equals(shopId))
            throw new ValidationException("Order belongs to a different shop");
        // then get the order items
        return orderMapper.toOrderItemDtoList(orderItemsRepository.findByOrderId(orderId));
    }

    @Override
    public List<OrderDto> getAllOrdersForCustomer(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        return createOrderDtoFromOrder(orders);
    }

    @Override
    public Page<OrderDto> getAllOrdersByMonth(int shopId, int year, int month, Pageable pageable) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay(); // exclusive

        Page<Order> orderDtoPage = orderRepository.findOrdersByMonth(startOfMonth, endOfMonth, pageable, shopId);
        return convertToDto(orderDtoPage);
    }

    @Override
    public OrderDto getOrderById(Long id) {
        return null;
    }

    @Override
    @Transactional
    public void cancelOrder(Long shopId, OrderUpdateDto order) {
        updateOrderStatus(order.getOrderId(), shopId, OrderStatusEnum.CANCELLED);
    }

    @Override
    @Transactional
    public OrderStatusEnum updateOrderStatusToNextState(Long shopId, OrderUpdateDto order) {

        OrderStatusSummary orderProjection = orderRepository.findOrderStatusSummaryById(order.getOrderId());

        if (!orderProjection.getVendorShopId().equals(shopId))
            throw new ValidationException("Order belongs to a different shop");
        OrderStatusEnum nextStatus = computeNextStatus(orderProjection.getStatus(), orderProjection.getOrderType());

        if (nextStatus == null)
            throw new ValidationException("Order status cannot be updated");
        updateOrderStatus(order.getOrderId(), shopId, nextStatus);
        return nextStatus;
    }

    @Override
    public OrdersTotalPerMonthDto getSalesSummaryForMonthForShop(Long shopId, int year, int month) {
        if (shopId == null || year == 0|| month == 0) {
            throw new ValidationException("Invalid Parameters");
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay();

        SalesSummary salesSummary = orderRepository.getTotalOrdersAndSalesByMonthForShop(startOfMonth, endOfMonth, shopId);
        OrdersTotalPerMonthDto ordersTotalPerMonthDto = new OrdersTotalPerMonthDto();
        ordersTotalPerMonthDto.setOrdersNumber(salesSummary.getCount());
        ordersTotalPerMonthDto.setTotalPrice(salesSummary.getTotal());
        return ordersTotalPerMonthDto;
    }

    @Override
    public PaymentStatusUpdate updateOrderPaymentStatus(PaymentStatusUpdate paymentStatusUpdate) {
        if (paymentStatusUpdate.getOrderId() == null ) throw new ValidationException("Order Id can't be null");
        Order order = orderRepository.findById(paymentStatusUpdate.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));
        if (order.getPaymentMethod().equals(PaymentMethodEnum.INSTAPAY)) {
            order.setPaymentStatus(paymentStatusUpdate.getPaymentStatus());
            return paymentStatusUpdate;
        } else {
            throw new ValidationException("Order status can't be updated");
        }
    }

    @Transactional
    public void saveOrder(Order order) {
        try {
            if (order == null) throw new ValidationException("Order cannot be null");
            orderRepository.save(order);
        } catch (Exception e) {
            throw new ValidationException("Failed to save order " + e.getMessage());
        }
    }

    @Transactional
    private void updateOrderStatus(Long orderId, Long shopId, OrderStatusEnum status) {
        try {
            orderRepository.updateOrderStatus(orderId, shopId, status);
        } catch (Exception e) {
            throw new ValidationException("Failed to update order status " + e.getMessage());
        }
    }

    public Order createOrderEntityFromCartAndOrderSummaryDto(CartAndOrderSummaryDto cartAndOrderSummaryDto) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());

        //Data From Cart Summary
        populateOrderFromCartSummary(order, cartAndOrderSummaryDto.getCartSummary());
        //Data From Order Summary
        populateOrderFromOrderSummary(order, cartAndOrderSummaryDto.getOrderSummary());

        order.setPaymentMethod(cartAndOrderSummaryDto.getOrderSummary().getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setStatus(OrderStatusEnum.PENDING);

        return order;
    }

    private List<OrderItem> createOrderItemsFromCartItems(List<CartItemDto> cartItems, Order order) {
        return cartItems.stream()
                .map(cartItemDto -> createOrderItemFromCartItem(cartItemDto, order))
                .toList();
    }

    private OrderItem createOrderItemFromCartItem(CartItemDto cartItemDto, Order order) {
        OrderItem orderItem = new OrderItem();
        Product product = entityManager.getReference(Product.class, cartItemDto.getProductId());
        orderItem.setQuantity(cartItemDto.getQuantity());
        orderItem.setProduct(product);
        orderItem.setUnitPrice(product.getPrice());
        orderItem.setTotalPrice(orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getQuantity())));
        orderItem.setCreatedAt(LocalDateTime.now());
        orderItem.setOrder(order);
        return orderItem;
    }

    private OrderStatusEnum computeNextStatus(OrderStatusEnum current, OrderTypeEnum orderType) {
        if (current == null || orderType == null)
            return null;

        return switch (current) {
            case PENDING -> OrderStatusEnum.PREPARING;
            case PREPARING -> (orderType == OrderTypeEnum.DELIVERY)
                    ? OrderStatusEnum.OUT_FOR_DELIVERY
                    : OrderStatusEnum.READY_FOR_PICKUP;
            case READY_FOR_PICKUP, OUT_FOR_DELIVERY -> OrderStatusEnum.DELIVERED;
            default -> null;
        };
    }

    private String generateOrderNumber() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

        // Random 4 chars for uniqueness
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            randomPart.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        return "ORD-" + datePart + "-" + randomPart;
    }

    private List<OrderDto> createOrderDtoFromOrder(List<Order> orders) {
        List<OrderDto> orderDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderDto orderDto = new OrderDto();
            orderDto.setId(order.getId());
            orderDto.setOrderNumber(order.getOrderNumber());
            orderDto.setOrderType(order.getOrderType());
            orderDto.setPaymentMethod(order.getPaymentMethod());
            orderDto.setStatus(order.getStatus());
            orderDto.setTotalPrice(order.getTotalPrice());
            orderDto.setCreatedAt(order.getCreatedAt());
            List<OrderItemDto> orderItemDtos = new ArrayList<>();
            order.getItems().forEach(orderItem -> {
                OrderItemDto orderItemDto = new OrderItemDto();
                orderItemDto.setId(orderItem.getId());
                orderItemDto.setName(orderItem.getProduct().getName());
                orderItemDto.setQuantity(orderItem.getQuantity());
                orderItemDto.setPrice(orderItem.getProduct().getPrice());
                orderItemDtos.add(orderItemDto);
            });

            orderDto.setItems(orderItemDtos);
            orderDtos.add(orderDto);
        }
        return orderDtos;
    }

    private List<ShopOrderDto> createShopOrderDtoFromOrder(List<Order> orders) {
        List<ShopOrderDto> orderDtos = new ArrayList<>();

        orders.forEach(order -> {
            List<OrderItemDto> items = new ArrayList<>();
            order.getItems().forEach(orderItem -> {
                OrderItemDto orderItemDto = new OrderItemDto();
                orderItemDto.setId(orderItem.getId());
                orderItemDto.setName(orderItem.getProduct().getName());
                orderItemDto.setQuantity(orderItem.getQuantity());
                orderItemDto.setPrice(orderItem.getProduct().getPrice());
                items.add(orderItemDto);
            });

            ShopOrderDto shopOrderDto = new ShopOrderDto(
                order.getId(),
                order.getOrderNumber(),
                order.getOrderType(),
                order.getPaymentMethod(),
                order.getStatus(),
                items,
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getCustomer().getId(),
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                order.getCustomer().getPhoneNumber(),

                order.getDeliveryAddress(),

                order.getLatitude(),
                order.getLongitude(),
                    false

        );
            orderDtos.add(shopOrderDto);
        });
        return orderDtos;
    
    }

    private Page<OrderDto> convertToDto(Page<Order> ordersPage) {
        List<OrderDto> orderDtos = ordersPage.getContent().stream().map(order -> {
            OrderDto dto = new OrderDto();
            dto.setId(order.getId());
            dto.setOrderNumber(order.getOrderNumber());
            dto.setOrderType(order.getOrderType());
            dto.setPaymentMethod(order.getPaymentMethod());
            dto.setStatus(order.getStatus());
            dto.setCreatedAt(order.getCreatedAt());
            dto.setTotalPrice(order.getTotalAmount());

            // Convert OrderItem to OrderItemDto
            List<OrderItemDto> itemDtos = order.getItems().stream().map(item -> {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setId(item.getId());
                itemDto.setName(item.getProduct().getName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getUnitPrice());
                return itemDto;
            }).collect(Collectors.toList());

            dto.setItems(itemDtos);
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(orderDtos, ordersPage.getPageable(), ordersPage.getTotalElements());
    }

    private void populateOrderFromCartSummary(Order order, CartSummaryDto cartSummaryDto) {
        order.setCustomer(
                entityManager.getReference(Customer.class, cartSummaryDto.getCustomerId()));
        order.setVendorShop(
                entityManager.getReference(VendorShop.class, cartSummaryDto.getShopId()));
    }

    private void populateOrderFromOrderSummary(Order order, OrderSummaryDto orderSummaryDto) {
        order.getItems()
                .addAll(createOrderItemsFromCartItems(orderSummaryDto.getItems(), order));
        OrderTypeBase type = orderSummaryDto.getOrderTypeBase();
        order.setOrderType(type.getOrderType());

        switch (type) {
            case DeliveryOrderTypeDto dto -> {
                order.setDeliveryAddress(dto.getAddress());
                order.setDeliveryFee(BigDecimal.valueOf(dto.getPrice()));
            }
            case PickupOrderTypeDto dto -> {
                order.setPickupTime(dto.getPickupTime());
                order.setDeliveryFee(BigDecimal.valueOf(0));
            }
            case InHouseOrderTypeDto ignored -> order.setDeliveryFee(BigDecimal.valueOf(0));
            default -> {
            }
        }
        order.setTotalAmount(BigDecimal.valueOf(orderSummaryDto.getTotal()));
    }
}
