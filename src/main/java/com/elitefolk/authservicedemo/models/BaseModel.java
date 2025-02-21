package com.elitefolk.authservicedemo.models;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@EnableJpaAuditing
public class BaseModel {
    @Id
    protected UUID id;

    @UpdateTimestamp
    private Long updatedDate;

    @CreationTimestamp
    private Long createdDate;

    private Boolean isDeleted = false;

    @PrePersist
    public void generateId() {
        if(id == null) {
            id = UuidCreator.getTimeOrdered();
        }
    }
}
