package com.elitefolk.authservicedemo.services;

import com.elitefolk.authservicedemo.models.SessionToken;

import java.util.List;

public interface SessionTokenService {
    SessionToken saveSessionToken(SessionToken sessionToken);
    SessionToken getSessionTokenByToken(String token);
    List<SessionToken> getActiveSessionTokenByUserId(Long userId);

}
