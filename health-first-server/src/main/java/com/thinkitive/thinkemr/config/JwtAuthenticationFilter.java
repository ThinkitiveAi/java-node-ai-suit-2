package com.thinkitive.thinkemr.config;

import com.thinkitive.thinkemr.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String token = extractTokenFromRequest(request);
            
            if (StringUtils.hasText(token) && jwtService.validateToken(token)) {
                String email = jwtService.extractEmail(token);
                String role = jwtService.extractRole(token);
                String providerId = jwtService.extractProviderId(token);
                
                // Create authentication token
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(role))
                );
                
                // Set additional details
                authentication.setDetails(Collections.singletonMap("providerId", providerId));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("JWT authentication successful for provider: {}", email);
            }
        } catch (Exception e) {
            log.warn("JWT authentication failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 