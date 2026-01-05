package com.peters.cafecart.workflows;

import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.AccountVerification.entity.ResetPasswordToken;
import com.peters.cafecart.features.AccountVerification.repository.ResetPasswordTokenRepository;
import com.peters.cafecart.features.CustomerManagement.entity.Customer;
import com.peters.cafecart.features.CustomerManagement.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordUseCase {

    @Autowired
    private ResetPasswordTokenRepository tokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(String token, String newPassword) {
        ResetPasswordToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ValidationException("Invalid or expired password reset token"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new ValidationException("Password reset token has expired");
        }

        Customer customer = resetToken.getCustomer();
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);

        tokenRepository.delete(resetToken);
    }
}
