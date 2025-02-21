package com.elitefolk.authservicedemo.services;

import com.elitefolk.authservicedemo.dtos.UserDetailsRequestDto;
import com.elitefolk.authservicedemo.dtos.UserDetailsResponseDto;
import com.elitefolk.authservicedemo.exceptions.UserDoesNotExistException;
import com.elitefolk.authservicedemo.models.SessionToken;
import com.elitefolk.authservicedemo.models.SessionTokenStatus;
import com.elitefolk.authservicedemo.models.TokenData;
import com.elitefolk.authservicedemo.models.User;
import com.elitefolk.authservicedemo.repositories.SessionTokenRepository;
import com.elitefolk.authservicedemo.repositories.UserRepository;
import com.elitefolk.authservicedemo.utils.ClientInfoUtil;
import com.elitefolk.authservicedemo.utils.EncodeDecodeUtil;
import com.elitefolk.authservicedemo.utils.TokenGeneratorUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepo;
    private SessionTokenRepository sessionTokenRepo;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepo,
                           SessionTokenRepository sessionTokenRepo,
                           PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.sessionTokenRepo = sessionTokenRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public UserDetailsResponseDto createUser(UserDetailsRequestDto userDetailsRequestDto, HttpServletRequest req) {
        User user = userDetailsRequestDto.toUser();
        user.setPassword(passwordEncoder.encode(userDetailsRequestDto.getPassword()));
        user = userRepo.save(user);
        SessionToken sessionToken = createNewActiveSessionToken(user, req);
        sessionTokenRepo.save(sessionToken);
        UserDetailsResponseDto dto = UserDetailsResponseDto.fromUser(user);
        dto.setToken(sessionToken.getToken());
        return dto;
    }

    @Override
    public Boolean existsByEmail(String email) {
        return this.userRepo.existsByEmail(email);
    }

    @Override
    public Boolean existsByMobileNumber(String mobileNumber) {
        return null;
    }

    @Override
    public UserDetailsResponseDto getUserByEmail(String email) {
        return null;
    }

    @Override
    public UserDetailsResponseDto getUserByMobileNumber(String mobileNumber) {
        return null;
    }

    @Override
    @Transactional
    public UserDetailsResponseDto validateUserLogin(String authHeader, HttpServletRequest req) {
        String authHeaderPart = authHeader.substring(6);
        String[] authParts = EncodeDecodeUtil.decodeBase64(authHeaderPart).split(":");
        String email = authParts[0];
        String password = authParts[1];
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserDoesNotExistException("User does not exist with email: " + email));
        if(user != null){
            boolean verified = passwordEncoder.matches(password, user.getPassword());
            if(verified){
                SessionToken sessionToken = createNewActiveSessionToken(user, req);
                sessionTokenRepo.save(sessionToken);
                UserDetailsResponseDto dto = UserDetailsResponseDto.fromUser(user);
                dto.setToken(sessionToken.getToken());
                return dto;
            }
        }
        return null;
    }

    public SessionToken createNewActiveSessionToken(User user, HttpServletRequest req){
        SessionToken sessionToken = new SessionToken();
        sessionToken.setUser(user);
        String token = TokenGeneratorUtil.generateTokenForUser(user);
        String userAgent = ClientInfoUtil.getBrowserId(req);
        String ipAddress = ClientInfoUtil.getClientIp(req);
        String device = ClientInfoUtil.getMachineId();
        sessionToken.setToken(token);
        sessionToken.setMachineId(device);
        sessionToken.setIpAddress(ipAddress);
        sessionToken.setBrowserId(userAgent);
        sessionToken.setStatus(SessionTokenStatus.ACTIVE);
        return sessionToken;
    }

    @Override
    public UserDetailsResponseDto validateToken(String token, HttpServletRequest req) {
        String tokenValue = EncodeDecodeUtil.decodeBase64(token);
        SessionToken sessionToken = sessionTokenRepo.findByToken(tokenValue);
        String userAgent = ClientInfoUtil.getBrowserId(req);
        String ipAddress = ClientInfoUtil.getClientIp(req);
        String device = ClientInfoUtil.getMachineId();
        if(sessionToken == null || !sessionToken.getBrowserId().equals(userAgent) || !sessionToken.getIpAddress().equals(ipAddress) || !sessionToken.getMachineId().equals(device)){
            return null;
        }
        if(sessionToken.getStatus() != SessionTokenStatus.ACTIVE){
            return null;
        }
        TokenData tokenData = TokenGeneratorUtil.decodeToken(token);
        if(tokenData.getExpiryDate() < System.currentTimeMillis()){
            sessionToken.setStatus(SessionTokenStatus.EXPIRED);
            sessionTokenRepo.save(sessionToken);
            return null;
        }
        return UserDetailsResponseDto.fromTokenData(tokenData);
    }

    @Override
    public Boolean logout(String token) {
        SessionToken sessionToken = sessionTokenRepo.findByToken(token);
        if(sessionToken != null && sessionToken.getStatus() == SessionTokenStatus.ACTIVE){
            sessionToken.setStatus(SessionTokenStatus.LOGGED_OUT);
            sessionTokenRepo.save(sessionToken);
            return true;
        }
        return false;
    }
}
