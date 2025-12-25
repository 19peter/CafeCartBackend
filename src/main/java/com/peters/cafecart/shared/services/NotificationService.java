package com.peters.cafecart.shared.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
@Service
public class NotificationService {
    @Autowired SimpMessagingTemplate messagingTemplate;

    public void notifyShopOfNewOrder(String shopId) {
        // Send to shop-specific topic
        messagingTemplate.convertAndSend(
                "/topic/shop/" + shopId + "/orders",
                "New Order"
        );
    }
}
