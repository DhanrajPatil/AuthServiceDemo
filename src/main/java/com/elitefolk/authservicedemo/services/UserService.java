package com.elitefolk.authservicedemo.services;

import com.elitefolk.authservicedemo.dtos.UserDetailsRequestDto;
import com.elitefolk.authservicedemo.dtos.UserDetailsResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    UserDetailsResponseDto createUser(UserDetailsRequestDto userDetailsRequestDto, HttpServletRequest req);
    Boolean existsByEmail(String email);
    Boolean existsByMobileNumber(String mobileNumber);

    UserDetailsResponseDto getUserByEmail(String email);
    UserDetailsResponseDto getUserByMobileNumber(String mobileNumber);
    UserDetailsResponseDto validateUserLogin(String authHeader, HttpServletRequest req);
    Boolean logout(String token);
    UserDetailsResponseDto validateToken(String token, HttpServletRequest req);
}
