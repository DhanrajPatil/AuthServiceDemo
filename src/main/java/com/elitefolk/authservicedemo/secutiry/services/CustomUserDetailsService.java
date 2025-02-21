package com.elitefolk.authservicedemo.secutiry.services;

import com.elitefolk.authservicedemo.models.User;
import com.elitefolk.authservicedemo.repositories.UserRepository;
import com.elitefolk.authservicedemo.secutiry.models.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(s)
                .orElseGet(() -> this.userRepository.findByMobileNumber(s).orElseGet(() -> {
                    throw new UsernameNotFoundException("User not found with email or mobile number: " + s);
                }));
        return new CustomUserDetails(user);
    }
}
