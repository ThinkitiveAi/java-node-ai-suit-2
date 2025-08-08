package com.thinkitive.thinkemr.service;

import com.thinkitive.thinkemr.config.JwtConfig;
import com.thinkitive.thinkemr.entity.Patient;
import com.thinkitive.thinkemr.entity.Provider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtConfig jwtConfig;

    public String generateToken(Provider provider) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("provider_id", provider.getId().toString());
        claims.put("email", provider.getEmail());
        claims.put("role", "PROVIDER");
        claims.put("specialization", provider.getSpecialization());
        claims.put("verification_status", provider.getVerificationStatus().toString());

        return createToken(claims, provider.getEmail());
    }

    public String generateToken(Patient patient) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("patient_id", patient.getId().toString());
        claims.put("email", patient.getEmail());
        claims.put("role", "PATIENT");
        claims.put("verification_status", patient.getVerificationStatus().toString());

        return createToken(claims, patient.getEmail());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (jwtConfig.getExpiration() * 1000));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(jwtConfig.getIssuer())
                .setAudience(jwtConfig.getAudience())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String extractProviderId(String token) {
        return extractAllClaims(token).get("provider_id", String.class);
    }

    public String extractPatientId(String token) {
        return extractAllClaims(token).get("patient_id", String.class);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractVerificationStatus(String token) {
        return extractAllClaims(token).get("verification_status", String.class);
    }
} 