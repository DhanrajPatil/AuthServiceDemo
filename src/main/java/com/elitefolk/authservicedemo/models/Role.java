package com.elitefolk.authservicedemo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "roles")
public class Role extends BaseModel{
    @Column(length = 20, nullable = false, unique = true)
    private String name;

}
