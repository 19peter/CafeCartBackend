package com.peters.cafecart.features.ProductsManagement.repository;

import com.peters.cafecart.features.ProductsManagement.entity.Product;
import com.peters.cafecart.features.ProductsManagement.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ProductOptionsRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findAllByIdIn(List<Long> ids);
}
