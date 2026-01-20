package com.peters.cafecart.features.AdditionsManagement.service;

import com.peters.cafecart.features.AdditionsManagement.dto.ShopAdditionDto;

import java.util.List;
import java.util.Set;

public interface ShopAdditionService {
    List<ShopAdditionDto> getAdditionsByShop(Long shopId);
    void updateAvailability(Long id, Boolean isAvailable, Long shopId);
    
    // Support methods for workflows
    void createShopAdditionsForNewAddition(Long additionId, Set<Long> shopIds);
    void createShopAdditionsForNewShop(Long shopId, List<Long> additionIds);
    void deleteShopAdditionsForAddition(Long additionId);
}
