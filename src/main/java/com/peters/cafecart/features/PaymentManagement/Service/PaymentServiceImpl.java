package com.peters.cafecart.features.PaymentManagement.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.peters.cafecart.features.CartManagement.dto.CartAndOrderSummaryDto;
import com.peters.cafecart.features.CartManagement.dto.CartItemDto;
import com.peters.cafecart.features.PaymentManagement.dto.PaymentPayloadDto;
import com.peters.cafecart.features.PaymentManagement.repository.PaymentInfoRepository;
import com.peters.cafecart.features.PaymentManagement.adapter.PaymentProvider;
import com.peters.cafecart.features.PaymentManagement.dto.PayloadBillingDetailsDto;
import com.peters.cafecart.features.PaymentManagement.dto.PayloadItemDto;
import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.features.PaymentManagement.projections.VendorPaymentInfo;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired PaymentInfoRepository paymentInfoRepository;
    @Autowired PaymentProvider paymentProvider;

    @Override
    public void createIntention(CartAndOrderSummaryDto cartAndOrderSummaryDto) {
     
        VendorPaymentInfo vendorPaymentInfo = paymentInfoRepository.findByVendorId(cartAndOrderSummaryDto.getCartSummary().getVendorId())
        .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
        
        PaymentPayloadDto payloadDto = getPaymentPayloadDto(cartAndOrderSummaryDto);
        payloadDto.setPrivate_key(vendorPaymentInfo.getPrivateKey());
        payloadDto.setPayment_methods(Arrays.asList(vendorPaymentInfo.getIntegrationId()));
        
        paymentProvider.createIntention(payloadDto);
    }


    @Override
    public void verifyWebhookSignature(String webhookData) {
        paymentProvider.verifyWebhookSignature(webhookData);
    }

    private PaymentPayloadDto getPaymentPayloadDto(CartAndOrderSummaryDto cartResponseDto) {
        PaymentPayloadDto payloadDto = new PaymentPayloadDto();
        payloadDto.setAmount(cartResponseDto.getOrderSummary().getTotal());
        payloadDto.setCurrency("EGP");
        payloadDto.setExpiration(3600);
        payloadDto.setNotification_url("https://webhook.site/dabe4968-5xxxxxxxxxxxxxxxxxxxxxx");
        payloadDto.setRedirection_url("https://www.google.com/");
        //set special reference to todays date + customer id + order id    
        payloadDto.setSpecial_reference("" + new Date().getTime() + cartResponseDto.getCartSummary().getCustomerId() + cartResponseDto.getCartSummary().getId());
        payloadDto.setExtras(new HashMap<>(){
            {
                put("shop_id", cartResponseDto.getCartSummary().getShopId());
            }
        });

        PayloadBillingDetailsDto billing = new PayloadBillingDetailsDto();
        payloadDto.setBilling_data(billing);

        ArrayList<PayloadItemDto> items = new ArrayList<>();
        for (CartItemDto cartItemDto : cartResponseDto.getOrderSummary().getItems()) {
            PayloadItemDto item = new PayloadItemDto();
            item.setName(cartItemDto.getProductId().toString());
            item.setAmount(cartItemDto.getUnitPrice());
            item.setDescription(cartItemDto.getProductId().toString());
            item.setQuantity(cartItemDto.getQuantity());
            items.add(item);
        }
        payloadDto.setItems(items);
        return payloadDto;
    }
}
