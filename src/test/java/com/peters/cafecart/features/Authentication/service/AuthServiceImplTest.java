package com.peters.cafecart.features.Authentication.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.peters.cafecart.config.CustomUserDetailsService;
import com.peters.cafecart.config.CustomUserPrincipal;
import com.peters.cafecart.config.JwtService;
import com.peters.cafecart.exceptions.CustomExceptions.UnauthorizedAccessException;
import com.peters.cafecart.exceptions.CustomExceptions.ValidationException;
import com.peters.cafecart.features.CustomerManagement.dto.CustomerDto;
import com.peters.cafecart.features.CustomerManagement.service.CustomerService;
import com.peters.cafecart.shared.dtos.Request.LoginRequest;
import com.peters.cafecart.shared.dtos.Response.AuthResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock private CustomUserDetailsService userDetailsService;
    @Mock private CustomerService customerService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @Mock private HttpServletResponse response;

    private CustomUserPrincipal user;

    @BeforeEach
    void setup() {
        user = mock(CustomUserPrincipal.class);

        // inject @Value fields
        ReflectionTestUtils.setField(authService, "secureCookie", false);
        ReflectionTestUtils.setField(authService, "httpOnlyCookie", true);
        ReflectionTestUtils.setField(authService, "sameSiteCookie", "Lax");
    }

    /* ---------- login ---------- */

    @Test
    void customerLogin_success() {
        LoginRequest req = new LoginRequest("a@b.com", "pass");

        when(userDetailsService.loadCustomerByUsername("a@b.com"))
                .thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("access");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh");

        ResponseEntity<AuthResponse> res =
                authService.customerLogin(req, response);

        assertEquals("access", res.getBody().accessToken());
        verify(response).addHeader(eq("Set-Cookie"), contains("refreshToken"));
    }

    @Test
    void vendorShopLogin_success() {
        when(userDetailsService.loadVendorShopByUsername(any()))
                .thenReturn(user);
        when(jwtService.generateToken(any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh");

        authService.vendorShopLogin(
                new LoginRequest("shop@x.com", "p"), response);

        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    void vendorLogin_success() {
        when(userDetailsService.loadVendorAccessAccountByUsername(any()))
                .thenReturn(user);
        when(jwtService.generateToken(any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh");

        authService.vendorLogin(
                new LoginRequest("v@x.com", "p"), response);

        verify(jwtService).generateToken(user);
    }

    /* ---------- refresh token ---------- */

    @Test
    void refreshToken_customer_success() {
        when(jwtService.isTokenValidForHandshake("rt")).thenReturn(true);
        when(jwtService.extractRole("rt")).thenReturn("CUSTOMER");
        when(jwtService.extractUsername("rt")).thenReturn("a@b.com");
        when(userDetailsService.loadCustomerByUsername(any()))
                .thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("new-access");

        ResponseEntity<AuthResponse> res =
                authService.refreshToken("rt");

        assertEquals("new-access", res.getBody().accessToken());
    }

    @Test
    void refreshToken_invalidRole() {
        when(jwtService.isTokenValidForHandshake("rt")).thenReturn(true);
        when(jwtService.extractRole("rt")).thenReturn("HACKER");

        assertThrows(ValidationException.class,
                () -> authService.refreshToken("rt"));
    }

    @Test
    void refreshToken_invalidToken() {
        when(jwtService.isTokenValidForHandshake("bad"))
                .thenReturn(false);

        assertThrows(UnauthorizedAccessException.class,
                () -> authService.refreshToken("bad"));
    }

    /* ---------- register ---------- */

    @Test
    void customerRegister_success() {
        CustomerDto dto = new CustomerDto();

        ResponseEntity<HttpStatus> res =
                authService.customerRegister(dto);

        verify(customerService).createCustomer(dto);
        assertEquals(HttpStatus.ACCEPTED, res.getBody());
    }

    /* ---------- token validation ---------- */

    @Test
    void isTokenValid_delegatesToJwtService() {
        when(jwtService.isTokenValidForHandshake("t"))
                .thenReturn(true);

        ResponseEntity<Boolean> res =
                authService.isTokenValid("t");

        assertTrue(res.getBody());
    }
}
