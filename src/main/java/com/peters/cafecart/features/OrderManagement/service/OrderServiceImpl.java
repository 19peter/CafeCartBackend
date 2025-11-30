package com.peters.cafecart.features.OrderManagement.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.OrderManagement.dto.OrderDto;
import com.peters.cafecart.features.OrderManagement.dto.OrderItemDto;
import com.peters.cafecart.features.OrderManagement.dto.OrderUpdateDto;
import com.peters.cafecart.features.OrderManagement.entity.Order;
import com.peters.cafecart.features.OrderManagement.entity.OrderItem;
import com.peters.cafecart.features.OrderManagement.enums.OrderStatusEnum;
import com.peters.cafecart.features.OrderManagement.enums.PaymentStatus;
import com.peters.cafecart.features.OrderManagement.projections.OrderStatusSummary;
import com.peters.cafecart.features.OrderManagement.repository.OrderItemsRepository;
import com.peters.cafecart.features.OrderManagement.repository.OrderRepository;
import com.peters.cafecart.shared.enums.OrderTypeEnum;
import com.peters.cafecart.shared.enums.PaymentMethodEnum;
import com.peters.cafecart.features.CartManagement.dto.CartAndOrderSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.CartItemDto;
import com.peters.cafecart.features.CartManagement.dto.CartOptionsDto;
import com.peters.cafecart.features.CartManagement.service.CartServiceImpl;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.InventoryManagement.service.InventoryService;
import com.peters.cafecart.features.OrderManagement.mapper.OrderMapper;
import com.peters.cafecart.features.PaymentManagement.Service.PaymentServiceImpl;
import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.VendorManagement.entity.VendorShop;
import com.peters.cafecart.features.VendorManagement.service.VendorShops.VendorShopsService;
import com.peters.cafecart.features.VendorManagement.service.VendorShops.VendorShopsServiceImpl;

@Service
public class OrderServiceImpl implements OrderService {
    SecureRandom RANDOM = new SecureRandom();
    @Autowired OrderRepository orderRepository;
    @Autowired OrderItemsRepository orderItemsRepository;
    
    @Autowired VendorShopsServiceImpl vendorShopsService;
    @Autowired CartServiceImpl cartService;
    @Autowired InventoryService inventoryService;
    @Autowired PaymentServiceImpl paymentService;
    @PersistenceContext EntityManager entityManager;
    @Autowired OrderMapper orderMapper;

    @Override
    public List<OrderDto> getAllOrdersForShop(Long shopId) {
        return orderMapper.toDtoList(orderRepository.findOrderDetailShopByShopId(shopId, PaymentStatus.PAID));
    }

    @Override
    public List<OrderItemDto> getOrderItems(Long shopId, Long orderId) {
        //First check that the order belongs to the shop
        Long orderShopId = orderRepository.findOrderShopIdById(orderId);
        if (orderShopId == null || !orderShopId.equals(shopId)) throw new ValidationException("Order belongs to a different shop");
        //then get the order items
        return orderMapper.toOrderItemDtoList(orderItemsRepository.findByOrderId(orderId));
    }

    @Override
    public List<OrderDto> getAllOrdersForCustomer(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return createOrderDtoFromOrder(orders);        
    }

    @Override
    public OrderDto getOrderById(Long id) {
        return null;
    }

    @Override
    @Transactional
    public void createOrder(Long customerId, CartOptionsDto cartOptionsDto) {
       CartAndOrderSummaryDto cartAndOrderSummaryDto =  cartService.getCartAndOrderSummary(customerId, cartOptionsDto);

       Optional<VendorShop> vendorShop = vendorShopsService.getVendorShop(cartAndOrderSummaryDto.getCartSummary().getShopId());
       if (vendorShop.isEmpty()) throw new ValidationException("Vendor shop not found");
       if (!vendorShop.get().getIsOnline()) throw new ValidationException("Vendor shop is not online");
       if (cartAndOrderSummaryDto.getOrderSummary().getOrderType() == OrderTypeEnum.DELIVERY && !vendorShop.get().isDeliveryAvailable()) throw new ValidationException("Delivery is not available for this shop");
       if (cartAndOrderSummaryDto.getOrderSummary().getPaymentMethod() == PaymentMethodEnum.CREDIT_CARD && !vendorShop.get().isOnlinePaymentAvailable()) throw new ValidationException("Online payment is not available for this shop");
       
       List<CartItemDto> cartItems = cartAndOrderSummaryDto.getOrderSummary().getItems();

       try {
           inventoryService.reduceInventoryStockInBulk(customerId, cartItems);
       } catch (Exception e) {
           throw new ValidationException("Failed to reduce inventory stock " + e.getMessage());
       }
       
       Order order = createOrderEntityFromCartAndOrderSummaryDto(cartAndOrderSummaryDto);
       
       if (cartAndOrderSummaryDto.getOrderSummary().getPaymentMethod() == PaymentMethodEnum.CREDIT_CARD) {
           order.setPaymentStatus(PaymentStatus.PENDING);
           try {
               paymentService.createIntention(cartAndOrderSummaryDto);
           } catch (Exception e) {
               throw new ValidationException("Failed to create payment intention " + e.getMessage());
           }
       }
       
       try {
           saveOrder(order);
           cartService.clearAllCartItems(customerId);
       } catch (Exception e) {
           throw new ValidationException("Failed to save order " + e.getMessage());
       }
    }

    @Override
    @Transactional
    public void updateOrderStatusToNextState(Long shopId, OrderUpdateDto order) {

        OrderStatusSummary orderProjection = orderRepository.findOrderStatusSummaryById(order.getOrderId());

        if (!orderProjection.getVendorShopId().equals(shopId)) throw new ValidationException("Order belongs to a different shop");
        OrderStatusEnum nextStatus = computeNextStatus(orderProjection.getStatus(), orderProjection.getOrderType());
        
        if (nextStatus == null) throw new ValidationException("Order status cannot be updated");
        updateOrderStatus(order.getOrderId(), shopId, nextStatus);
    }

    @Transactional
    private void saveOrder(Order order) {
        try {
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
            throw new ValidationException("Failed to update order status" + e.getMessage());
        }
    }

    private Order createOrderEntityFromCartAndOrderSummaryDto(CartAndOrderSummaryDto cartAndOrderSummaryDto) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(entityManager.getReference(Customer.class, cartAndOrderSummaryDto.getCartSummary().getCustomerId()));
        order.setVendorShop(entityManager.getReference(VendorShop.class, cartAndOrderSummaryDto.getCartSummary().getShopId()));
        order.setOrderType(cartAndOrderSummaryDto.getOrderSummary().getOrderType());
        order.getItems().addAll(createOrderItemsFromCartItems(cartAndOrderSummaryDto.getOrderSummary().getItems(), order));
        order.setDeliveryAddress("abc");
        order.setPaymentMethod(cartAndOrderSummaryDto.getOrderSummary().getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setStatus(OrderStatusEnum.PENDING);
        order.setTotalAmount(new BigDecimal(cartAndOrderSummaryDto.getOrderSummary().getTotal()));
        order.setDeliveryFee(new BigDecimal(cartAndOrderSummaryDto.getOrderSummary().getDeliveryFee()));
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
        orderItem.setSpecialInstructions(null);
        orderItem.setOrder(order);
        return orderItem;
    }

    private OrderStatusEnum computeNextStatus(OrderStatusEnum current, OrderTypeEnum orderType) {
        if (current == null || orderType == null)
            return null;

        switch (current) {
            case PENDING:
                return OrderStatusEnum.PREPARING;
            case PREPARING:
                return (orderType == OrderTypeEnum.DELIVERY)
                        ? OrderStatusEnum.OUT_FOR_DELIVERY
                        : OrderStatusEnum.READY_FOR_PICKUP;
            case READY_FOR_PICKUP:
            case OUT_FOR_DELIVERY:
                return OrderStatusEnum.DELIVERED;
            case DELIVERED:
            case CANCELLED:
            default:
                return OrderStatusEnum.PREPARING;
        }
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
            List<OrderItemDto> orderItemDtos = new ArrayList<>();
            order.getItems().forEach(orderItem -> {
                OrderItemDto orderItemDto = new OrderItemDto();
                orderItemDto.setId(orderItem.getId());
                orderItemDto.setName(orderItem.getProduct().getName());
                orderItemDto.setQuantity(orderItem.getQuantity());
                orderItemDto.setPrice(orderItem.getProduct().getPrice());
                orderItemDto.setSpecialInstructions(orderItem.getSpecialInstructions());
                orderItemDtos.add(orderItemDto);
            });

            orderDto.setItems(orderItemDtos);
            orderDtos.add(orderDto);
        }
        return orderDtos;
    }

}
