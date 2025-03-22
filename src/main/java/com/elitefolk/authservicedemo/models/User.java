package com.elitefolk.authservicedemo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
@Table( indexes = {
        @Index(name = "email_index", columnList = "email"),
        @Index(name = "mobile_number_index", columnList = "mobileNumber")
})
public class User extends BaseModel{
    @Column(length = 30, nullable = false)
    private String firstName;

    @Column(length = 30, nullable = false)
    private String lastName;

    @Column(length = 200, nullable = false)
    private String password;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 15, unique = true)
    private String mobileNumber;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    private List<Role> roles;
}
