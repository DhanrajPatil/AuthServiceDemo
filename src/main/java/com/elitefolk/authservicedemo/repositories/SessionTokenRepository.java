package com.elitefolk.authservicedemo.repositories;

import com.elitefolk.authservicedemo.models.SessionToken;
import com.elitefolk.authservicedemo.models.SessionTokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SessionTokenRepository extends JpaRepository<SessionToken, UUID> {
    SessionToken findByToken(String token);
    Boolean existsByToken(String token);
    Boolean existsByUser_IdAndStatus(UUID user_id, SessionTokenStatus status);
}
