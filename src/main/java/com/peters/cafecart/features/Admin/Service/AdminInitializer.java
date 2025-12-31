package com.peters.cafecart.features.Admin.Service;

import com.peters.cafecart.features.Admin.entity.Admin;
import com.peters.cafecart.features.Admin.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {
    @Autowired AdminRepository adminRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Value("${admin.default.email}") String email;
    @Value("${admin.default.password}") String password;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!adminRepository.existsByEmail(email)) {
            Admin admin = new Admin();
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            adminRepository.save(admin);
        }
    }
}
