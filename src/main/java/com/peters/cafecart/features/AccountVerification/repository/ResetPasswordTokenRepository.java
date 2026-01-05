package com.peters.cafecart.features.AccountVerification.repository;

import com.peters.cafecart.features.AccountVerification.entity.ResetPasswordToken;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    Optional<ResetPasswordToken> findByToken(String token);
    Optional<ResetPasswordToken> findByCustomer(Customer customer);
}
