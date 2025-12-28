package com.peters.cafecart.features.PaymentManagement.Service;

import com.peters.cafecart.features.CartManagement.dto.response.CartAndOrderSummaryDto;

public interface PaymentService {
    
    public void createIntention(CartAndOrderSummaryDto cartAndOrderSummaryDto);
    public void verifyWebhookSignature(String webhookData);
}
