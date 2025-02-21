package com.elitefolk.authservicedemo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="tokens")
@Table(indexes = {
        @Index(name = "token_index", columnList = "token")
})
public class SessionToken extends BaseModel {
    @Column(nullable = false, length = 500)
    private String token;

    @ManyToOne
    private User user;

    @Column(length = 32)
    private String ipAddress;
    @Column(length = 32)
    private String machineId;
    @Column(length = 150)
    private String browserId;

    @Enumerated(value = EnumType.ORDINAL)
    private SessionTokenStatus status;

}
