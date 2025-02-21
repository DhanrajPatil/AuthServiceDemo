package com.elitefolk.authservicedemo.controllers;

import com.elitefolk.authservicedemo.dtos.TokenDto;
import com.elitefolk.authservicedemo.dtos.UserDetailsRequestDto;
import com.elitefolk.authservicedemo.dtos.UserDetailsResponseDto;
import com.elitefolk.authservicedemo.secutiry.models.CustomUserDetails;
import com.elitefolk.authservicedemo.services.TokenService;
import com.elitefolk.authservicedemo.services.UserService;
import com.elitefolk.authservicedemo.utils.EncodeDecodeUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService; // Service to generate OAuth2 token

    public AuthController(UserService userService, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDetailsResponseDto> signUp(@Valid @RequestBody UserDetailsRequestDto userDetailsRequestDto,
                                                         @RequestHeader("Authorization") String authHeader,
                                                         HttpServletRequest req) {
        if(authHeader == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            String authHeaderPart = authHeader.substring(6);
            String[] authParts = EncodeDecodeUtil.decodeBase64(authHeaderPart).split(":");
            if(authParts.length != 2) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            userDetailsRequestDto.setPassword(authParts[1]);
            userDetailsRequestDto.setEmail(authParts[0]);
        }
        UserDetailsResponseDto dto = userService.createUser(userDetailsRequestDto, req);
        if(dto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            String token = dto.getToken();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", EncodeDecodeUtil.encodeBase64(token));
            dto.setToken(null);
            return new ResponseEntity<>(dto, headers, HttpStatus.OK);
        }
    }

    @GetMapping("/email-exists/{email}")
    public Boolean emailExists(@PathVariable String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/mobile-exists/{mobileNumber}")
    public Boolean mobileExists(@PathVariable String mobileNumber) {
        return userService.existsByMobileNumber(mobileNumber);
    }

    @PostMapping("/login")
    public ResponseEntity<CustomUserDetails> login(@RequestHeader("Authorization") String authHeader, HttpServletRequest req, HttpServletResponse response) {
        String authHeaderPart = authHeader.substring(6);
        String[] authParts = EncodeDecodeUtil.decodeBase64(authHeaderPart).split(":");
        String email = authParts[0];
        String password = authParts[1];
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // Generate token using Spring Authorization Server
        String token = tokenService.generateToken(authentication);

        // Set the token in HTTP-only cookie
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production (requires HTTPS)
        cookie.setPath("/");
        response.addCookie(cookie);
        cookie.setDomain("localhost"); // Important: Set domain properly
        cookie.setMaxAge(60 * 4); // 15 minutes token validity
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userDetails.setPassword(null);

        return ResponseEntity.ok(userDetails);
    }

    @PostMapping("/logout")
    public Boolean logout(@RequestBody TokenDto tokenDto) {
        return userService.logout(tokenDto.getToken());
    }

    @PostMapping("/validate-token")
    public ResponseEntity<UserDetailsResponseDto> validateToken(@RequestHeader("Authorization") String authHeader, HttpServletRequest req) {
        UserDetailsResponseDto dto = userService.validateToken(authHeader, req);
        if(dto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(dto);
    }
}
