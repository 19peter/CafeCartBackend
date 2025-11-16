package com.peters.cafecart.features.PaymentManagement.adapter;

import com.peters.cafecart.features.PaymentManagement.dto.PaymentPayloadDto;

public interface PaymentProvider {
    public void createIntention(PaymentPayloadDto request);

    public boolean verifyWebhookSignature(String webhookData);
}
