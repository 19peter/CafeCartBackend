package com.peters.cafecart.features.AdditionsManagement.repository;

import com.peters.cafecart.features.AdditionsManagement.entity.AdditionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdditionGroupRepository extends JpaRepository<AdditionGroup, Long> {
    List<AdditionGroup> findByVendor_Id(Long vendorId);
    Optional<AdditionGroup> findByIdAndVendor_Id(Long id, Long vendorId);
    boolean existsByNameAndVendor_Id(String name, Long vendorId);

    List<AdditionGroup>findAllByVendor_IdAndIdIn(Long vendorId, List<Long> ids);
}
