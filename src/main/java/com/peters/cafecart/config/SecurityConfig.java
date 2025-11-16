package com.peters.cafecart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import com.peters.cafecart.Constants.Constants;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    JwtAuthFilter jwtAuthFilter;

    @Bean
    AuthenticationManager authenticationManager(
            HttpSecurity http,
            PasswordEncoder encoder,
            CustomUserDetailsService customUserDetailsService) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(encoder);

        return authenticationManagerBuilder.build();
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider(
            CustomUserDetailsService customUserDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disable CSRF for APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                Constants.CUSTOMER_AUTH_LOGIN,
                                Constants.CUSTOMER_AUTH_REGISTER,
                                Constants.CUSTOMER_AUTH_REFRESH_TOKEN,
                                Constants.VENDOR_AUTH_LOGIN,
                                Constants.VENDOR_AUTH_REGISTER,
                                Constants.VENDOR_AUTH_REFRESH_TOKEN,
                                Constants.SHOP_AUTH_LOGIN,
                                Constants.SHOP_AUTH_REGISTER,
                                Constants.SHOP_AUTH_REFRESH_TOKEN,
                                Constants.CURRENT_API + "/vendors/**",
                                Constants.API_V1 + "/vendor-shops/**",
                                Constants.API_V1 + "/inventory/**"
                                )
                        .permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(daoAuthenticationProvider)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}