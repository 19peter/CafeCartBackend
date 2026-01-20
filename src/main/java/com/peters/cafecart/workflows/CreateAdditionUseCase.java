package com.peters.cafecart.workflows;

import com.peters.cafecart.features.AdditionsManagement.dto.AdditionDto;
import com.peters.cafecart.features.AdditionsManagement.service.AdditionGroupService;
import com.peters.cafecart.features.AdditionsManagement.service.ShopAdditionService;
import com.peters.cafecart.features.VendorManagement.service.VendorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateAdditionUseCase {

    private final AdditionGroupService additionGroupService;
    private final ShopAdditionService shopAdditionService;
    private final VendorService vendorService;

    @Transactional
    public AdditionDto execute(Long groupId, AdditionDto additionDto, Long vendorId) {
        log.info("Executing CreateAdditionUseCase for group {} and vendor {}", groupId, vendorId);
        
        // 1. Create the master Addition entry
        AdditionDto createdAddition = additionGroupService.addAddition(groupId, additionDto, vendorId);
        
        // 2. Get all shops for this vendor
        Set<Long> shopIds = vendorService.getShopIdsByVendorId(vendorId);
        
        // 3. Create ShopAddition projections for each shop
        shopAdditionService.createShopAdditionsForNewAddition(createdAddition.getId(), shopIds);
        
        log.info("Successfully created addition {} and synchronized with {} shops", createdAddition.getId(), shopIds.size());
        return createdAddition;
    }
}
