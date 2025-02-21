package com.elitefolk.authservicedemo.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenData {
    private String token;
    private String userId;
    private String name;
    private String email;
    private String mobileNumber;
    private Long expiryDate;
    private List<String> Roles = new ArrayList<>();
}
