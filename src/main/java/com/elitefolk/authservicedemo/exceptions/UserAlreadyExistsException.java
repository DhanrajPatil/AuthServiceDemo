package com.elitefolk.authservicedemo.exceptions;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private String email;
    public UserAlreadyExistsException(String email) {
        super(String.format("User with %s already exists", email));
        this.email = email;
    }
}
