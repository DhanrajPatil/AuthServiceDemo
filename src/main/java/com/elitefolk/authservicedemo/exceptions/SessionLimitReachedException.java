package com.elitefolk.authservicedemo.exceptions;

public class SessionLimitReachedException extends RuntimeException{

    public SessionLimitReachedException(String message) {
        super(message);
    }
}
