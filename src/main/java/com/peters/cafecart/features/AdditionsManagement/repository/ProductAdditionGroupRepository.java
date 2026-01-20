package com.peters.cafecart.features.AdditionsManagement.repository;

import com.peters.cafecart.features.AdditionsManagement.entity.ProductAdditionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAdditionGroupRepository extends JpaRepository<ProductAdditionGroup, Long> {
    List<ProductAdditionGroup> findByProduct_Id(Long productId);
    void deleteByProduct_IdAndAdditionGroup_Id(Long productId, Long groupId);
}
