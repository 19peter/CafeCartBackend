package com.peters.cafecart.features.Admin.repository;

import com.peters.cafecart.features.Admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByEmail(String email);
    Optional<Admin> findByEmail(String email);
}
