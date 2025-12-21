package com.peters.cafecart.features.PaymentManagement.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.PaymentManagement.dto.PaymentPayloadDto;

@Component
public class PaymobPaymentProvider implements PaymentProvider {
    @Value("${payment.url}") private String paymentUrl;

    @Override
    public void createIntention(PaymentPayloadDto request) {
        String url = paymentUrl;
        if (url == null) throw new ValidationException("Payment URL is not configured");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Token " + request.getPrivate_key());
        request.setPrivate_key(null);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<PaymentPayloadDto> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        System.out.println(response.getBody());
    }

    @Override
    public boolean verifyWebhookSignature(String webhookData) {
        return false;
    }


}
