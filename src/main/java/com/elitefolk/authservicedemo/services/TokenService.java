package com.elitefolk.authservicedemo.services;

import com.elitefolk.authservicedemo.secutiry.models.CustomGrantedAuthority;
import com.elitefolk.authservicedemo.secutiry.models.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = details.getAuthorities().stream().map(CustomGrantedAuthority::getAuthority).toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject(username)
                .audience(List.of("film-rental-service", "product-service", "auth-service"))
                .claim("name", details.getName())
                .claim("roles", roles)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(180)) // 3 minutes
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}