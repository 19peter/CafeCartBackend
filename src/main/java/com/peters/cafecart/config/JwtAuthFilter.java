package com.peters.cafecart.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.peters.cafecart.Constants.Constants;
import com.peters.cafecart.exceptions.CustomExceptions.UnauthorizedAccessException;

import org.springframework.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (Constants.ALLOWED_PATHS.contains(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = resolveToken(authHeader);

        try {
            final String email = jwtService.extractUsername(token);
            final String id = jwtService.extractUserId(token);
            final String role = jwtService.extractRole(token);

            if (email == null || id == null || role == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                CustomUserPrincipal userDetails = new CustomUserPrincipal(
                        Long.valueOf(id), email, null,
                        true, true, true, true,
                        List.of(new SimpleGrantedAuthority(role)));

                if (jwtService.isTokenValid(token, userDetails)) {
                    authenticate(request, userDetails);
                }
            }

            filterChain.doFilter(request, response);

        } catch (UnauthorizedAccessException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
        }
    }

    private String resolveToken(String authHeader) {
        return authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
    }

    private void authenticate(HttpServletRequest request, CustomUserPrincipal userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
