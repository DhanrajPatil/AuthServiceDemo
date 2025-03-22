package com.elitefolk.authservicedemo.services;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String username = authentication.getName();
        List<String> roles = new ArrayList<>();
        if(authentication.getAuthorities().isEmpty()) {
            roles = List.of("User");
        } else {
            roles = authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        }
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject(username)
                .audience(List.of("film-rental-service", "product-service", "auth-service"))
                .claim("name", username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(300)) // 5 minutes
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public ResponseCookie embedTokenInCookie(String token) {
        ResponseCookie jwtCookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(true)  // Required for SameSite=None
                .domain("localhost") // Ensures subdomains can access it
                .path("/")
                .sameSite("None")  // Required for cross-origin requests
                .maxAge(300)  // 5 minutes
                .build();
        return jwtCookie;
    }
}