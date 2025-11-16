package com.peters.cafecart.features.DeliveryManagment.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.features.DeliveryManagment.dto.CustomerLocationRequestDto;
import com.peters.cafecart.features.DeliveryManagment.service.DeliveryServiceImpl;

@RestController
@RequestMapping(Constants.API_V1 + "/delivery-settings")
public class DeliverySettingsController {
    @Autowired
    DeliveryServiceImpl deliveryService;

    @GetMapping
    public double calculateDeliveryCost(@RequestBody CustomerLocationRequestDto customerLocationRequestDto) {
        return deliveryService.calculateDeliveryCost(customerLocationRequestDto);
    }
}
