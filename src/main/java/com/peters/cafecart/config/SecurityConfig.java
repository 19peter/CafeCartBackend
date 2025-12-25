package com.peters.cafecart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Allow WebSocket endpoints
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/app/**").permitAll()
                        .requestMatchers("/topic/**").permitAll()
                        .requestMatchers("/queue/**").permitAll()
                        .requestMatchers(
                                Constants.CURRENT_API + "/cart/**",
                                Constants.CURRENT_API + "/orders/customer/**"
                                ).hasRole("CUSTOMER")

                        .requestMatchers(
                                Constants.CURRENT_API + "/orders/vendor/**",
                                Constants.CURRENT_API + "/vendors/vendor/**"
                        ).hasRole("VENDOR")


                        .requestMatchers(
                                Constants.CURRENT_API + "/orders/shop/**",
                                Constants.CURRENT_API + "/vendor-shops/shop/**",
                                Constants.CURRENT_API + "/products/shop/**",
                                Constants.CURRENT_API + "/shop-products/shop/**"
                                ).hasRole("SHOP")
                        .anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}