package com.elitefolk.authservicedemo.secutiry.models;

import com.elitefolk.authservicedemo.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class CustomUserDetails implements UserDetails {
    private String username;
    private String password;
    private String mobileNumber;
    private String name;
    private List<CustomGrantedAuthority> authorities;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;


    public CustomUserDetails(User user) {
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.mobileNumber = user.getMobileNumber();
        this.name = user.getFirstName() + " " + user.getLastName();
        this.authorities = new ArrayList<>();
        for(var role: user.getRoles()) {
            this.authorities.add(new CustomGrantedAuthority(role));
        }
    }
}
