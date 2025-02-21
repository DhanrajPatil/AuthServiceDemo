package com.elitefolk.authservicedemo.dtos;

import com.elitefolk.authservicedemo.models.User;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsRequestDto {
    @NonNull
    @Pattern(regexp = "^[a-zA-Z]{3,30}$", message = "First name should contain only alphabets and should be between 3 to 30 characters")
    private String firstName;

    @NonNull
    @Pattern(regexp = "^[a-zA-Z]{3,30}$", message = "Last name should contain only alphabets and should be between 3 to 30 characters")
    private String lastName;

    private String email;

    @NonNull
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid Mobile Number")
    private String mobileNumber;

    private String password;

    public User toUser() {
        User user = new User();
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setEmail(this.email);
        user.setMobileNumber(this.mobileNumber);
        user.setPassword(this.password);
        return user;
    }
}
