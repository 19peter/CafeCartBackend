package com.peters.cafecart.features.AdditionsManagement.service;

import com.peters.cafecart.exceptions.CustomExceptions.ResourceNotFoundException;
import com.peters.cafecart.exceptions.CustomExceptions.UnauthorizedAccessException;
import com.peters.cafecart.features.AdditionsManagement.dto.ShopAdditionDto;
import com.peters.cafecart.features.AdditionsManagement.entity.Addition;
import com.peters.cafecart.features.AdditionsManagement.entity.ShopAddition;
import com.peters.cafecart.features.AdditionsManagement.mapper.AdditionMapper;
import com.peters.cafecart.features.AdditionsManagement.repository.ShopAdditionRepository;
import com.peters.cafecart.features.ShopManagement.entity.VendorShop;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopAdditionServiceImpl implements ShopAdditionService {

    private final ShopAdditionRepository shopAdditionRepository;
    private final AdditionMapper mapper;
    private final EntityManager entityManager;

    @Override
    public List<ShopAdditionDto> getAdditionsByShop(Long shopId) {
        return mapper.toShopAdditionDtoList(shopAdditionRepository.findByShop_Id(shopId));
    }

    @Override
    public void updateAvailability(Long id, Boolean isAvailable, Long shopId) {
        ShopAddition shopAddition = shopAdditionRepository.findByIdAndShop_Id(id, shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop addition not found or you do not have access"));
        
        shopAddition.setIsAvailable(isAvailable);
        shopAdditionRepository.save(shopAddition);
    }

    @Override
    public void createShopAdditionsForNewAddition(Long additionId, Set<Long> shopIds) {
        Addition addition = entityManager.getReference(Addition.class, additionId);
        for (Long shopId : shopIds) {
            ShopAddition sa = new ShopAddition();
            sa.setShop(entityManager.getReference(VendorShop.class, shopId));
            sa.setAddition(addition);
            sa.setIsAvailable(true);
            shopAdditionRepository.save(sa);
        }
    }

    @Override
    public void createShopAdditionsForNewShop(Long shopId, List<Long> additionIds) {
        VendorShop shop = entityManager.getReference(VendorShop.class, shopId);
        for (Long additionId : additionIds) {
            ShopAddition sa = new ShopAddition();
            sa.setShop(shop);
            sa.setAddition(entityManager.getReference(Addition.class, additionId));
            sa.setIsAvailable(true);
            shopAdditionRepository.save(sa);
        }
    }

    @Override
    public void deleteShopAdditionsForAddition(Long additionId) {
        shopAdditionRepository.deleteByAddition_Id(additionId);
    }
}
