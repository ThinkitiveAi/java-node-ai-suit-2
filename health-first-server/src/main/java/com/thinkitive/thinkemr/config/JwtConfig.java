package com.thinkitive.thinkemr.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret = "your-256-bit-secret-key-here-for-jwt-signing-and-verification";
    private long expiration = 3600; // 1 hour in seconds
    private String issuer = "health-first-server";
    private String audience = "health-first-providers";
} 