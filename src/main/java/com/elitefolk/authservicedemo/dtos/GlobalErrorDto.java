package com.elitefolk.authservicedemo.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GlobalErrorDto<T> {
    private String message;
    private String status;
    private T errorData;
}
