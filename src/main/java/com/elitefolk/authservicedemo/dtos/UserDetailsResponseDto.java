package com.elitefolk.authservicedemo.dtos;

import com.elitefolk.authservicedemo.models.Role;
import com.elitefolk.authservicedemo.models.TokenData;
import com.elitefolk.authservicedemo.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UserDetailsResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private String token;
    private List<String> roles = new ArrayList<>();

    public static UserDetailsResponseDto fromUser(User user) {
        UserDetailsResponseDto userDetailsResponseDto = new UserDetailsResponseDto();
        userDetailsResponseDto.setId(user.getId());
        userDetailsResponseDto.setFirstName(user.getFirstName());
        userDetailsResponseDto.setLastName(user.getLastName());
        userDetailsResponseDto.setEmail(user.getEmail());
        userDetailsResponseDto.setMobileNumber(user.getMobileNumber());
        if(user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                userDetailsResponseDto.getRoles().add(role.getName());
            }
        }
        return userDetailsResponseDto;
    }

    public static UserDetailsResponseDto fromTokenData(TokenData tokenData) {
        UserDetailsResponseDto userDetailsResponseDto = new UserDetailsResponseDto();
        userDetailsResponseDto.setToken(tokenData.getToken());
        userDetailsResponseDto.setId(UUID.fromString(tokenData.getUserId()));
        String name[] = tokenData.getName().split(" ");
        userDetailsResponseDto.setFirstName(name[0]);
        userDetailsResponseDto.setLastName(name[1]);
        userDetailsResponseDto.setEmail(tokenData.getEmail());
        userDetailsResponseDto.setMobileNumber(tokenData.getMobileNumber());
        userDetailsResponseDto.setRoles(tokenData.getRoles());
        return userDetailsResponseDto;
    }
}
