package com.peters.cafecart.features.AdditionsManagement.repository;

import com.peters.cafecart.features.AdditionsManagement.entity.Addition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdditionRepository extends JpaRepository<Addition, Long> {
    Optional<Addition> findByIdAndAdditionGroup_Vendor_Id(Long id, Long vendorId);
}
