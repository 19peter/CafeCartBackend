package com.peters.cafecart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.config.Customizer;

import org.springframework.beans.factory.annotation.Autowired;
import com.peters.cafecart.Constants.Constants;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    JwtAuthFilter jwtAuthFilter;

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // Constants.CUSTOMER_AUTH_LOGIN,
                                // Constants.CUSTOMER_AUTH_REGISTER,
                                // Constants.CUSTOMER_AUTH_REFRESH_TOKEN,
                                // Constants.VENDOR_AUTH_LOGIN,
                                // Constants.VENDOR_AUTH_REGISTER,
                                // Constants.VENDOR_AUTH_REFRESH_TOKEN,
                                // Constants.SHOP_AUTH_LOGIN,
                                // Constants.SHOP_AUTH_REGISTER,
                                // Constants.SHOP_AUTH_REFRESH_TOKEN,
                                // Constants.CURRENT_API + "/vendors/**",
                                // Constants.CURRENT_API + "/vendor-shops/**",
                                // Constants.CURRENT_API + "/inventory/**",
                                Constants.CURRENT_API + "/cart/**",
                                Constants.CURRENT_API + "/orders/**"
                                )
                                .hasRole("CUSTOMER")
                        .anyRequest()
                        .permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}