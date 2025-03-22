package com.elitefolk.authservicedemo.advices;

import com.elitefolk.authservicedemo.dtos.GlobalErrorDto;
import com.elitefolk.authservicedemo.exceptions.SessionLimitReachedException;
import com.elitefolk.authservicedemo.exceptions.UserAlreadyExistsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvices {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public GlobalErrorDto<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return new GlobalErrorDto<>("Validation Error", "400", ex.getMessage());
    }

    @ExceptionHandler(SessionLimitReachedException.class)
    public GlobalErrorDto<String> handleSessionLimitReachedException(SessionLimitReachedException ex) {
        return new GlobalErrorDto<>("Session Limit Reached", "401", ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public GlobalErrorDto<String> UserAlreadyExistsExceptionHandler(UserAlreadyExistsException e) {
        return new GlobalErrorDto<>(e.getMessage(), "409", e.getEmail());
    }

}
