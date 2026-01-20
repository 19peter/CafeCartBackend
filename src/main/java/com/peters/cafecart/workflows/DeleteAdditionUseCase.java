package com.peters.cafecart.workflows;

import com.peters.cafecart.features.AdditionsManagement.service.AdditionGroupService;
import com.peters.cafecart.features.AdditionsManagement.service.ShopAdditionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteAdditionUseCase {

    private final AdditionGroupService additionGroupService;
    private final ShopAdditionService shopAdditionService;

    @Transactional
    public void execute(Long additionId, Long vendorId) {
        log.info("Executing DeleteAdditionUseCase for addition {} and vendor {}", additionId, vendorId);
        
        // 1. Delete shop projections first (optional depending on DB constraints, but safer)
        shopAdditionService.deleteShopAdditionsForAddition(additionId);
        
        // 2. Delete master master Entry
        additionGroupService.deleteAddition(additionId, vendorId);
        
        log.info("Successfully deleted addition {} and its projections", additionId);
    }
}
