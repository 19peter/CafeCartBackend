package com.peters.cafecart.features.ProductsManagement.repository;

import com.peters.cafecart.features.ProductsManagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
