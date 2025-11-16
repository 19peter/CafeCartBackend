package com.peters.cafecart.features.PaymentManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.features.PaymentManagement.Service.PaymentServiceImpl;

@RestController("/payment")
public class PaymentController {
    @Autowired PaymentServiceImpl paymentService;

    @PostMapping(Constants.CURRENT_API + "/webhook")
    public void webhook(@RequestBody String webhookData) {
        paymentService.verifyWebhookSignature(webhookData);
    }

}
