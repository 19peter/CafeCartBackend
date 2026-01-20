package com.peters.cafecart.features.AdditionsManagement.repository;

import com.peters.cafecart.features.AdditionsManagement.entity.ShopAddition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopAdditionRepository extends JpaRepository<ShopAddition, Long> {
    List<ShopAddition> findByShop_Id(Long shopId);
    Optional<ShopAddition> findByIdAndShop_Vendor_Id(Long id, Long vendorId);
    Optional<ShopAddition> findByIdAndShop_Id(Long id, Long shopId);
    void deleteByAddition_Id(Long additionId);
}
