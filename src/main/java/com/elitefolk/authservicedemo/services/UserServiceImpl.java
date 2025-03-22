package com.elitefolk.authservicedemo.services;

import com.elitefolk.authservicedemo.dtos.UserDetailsRequestDto;
import com.elitefolk.authservicedemo.dtos.UserDetailsResponseDto;
import com.elitefolk.authservicedemo.exceptions.UserAlreadyExistsException;
import com.elitefolk.authservicedemo.exceptions.UserDoesNotExistException;
import com.elitefolk.authservicedemo.models.*;
import com.elitefolk.authservicedemo.repositories.SessionTokenRepository;
import com.elitefolk.authservicedemo.repositories.UserRepository;
import com.elitefolk.authservicedemo.utils.ClientInfoUtil;
import com.elitefolk.authservicedemo.utils.EncodeDecodeUtil;
import com.elitefolk.authservicedemo.utils.TokenGeneratorUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepo;
    private SessionTokenRepository sessionTokenRepo;
    private PasswordEncoder passwordEncoder;
    private GoogleAuthService googleAuthService;
    private TokenService tokenService;
    AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepo,
                           SessionTokenRepository sessionTokenRepo,
                           PasswordEncoder passwordEncoder,
                           GoogleAuthService googleAuthService,
                           TokenService tokenService,
                           AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.sessionTokenRepo = sessionTokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.googleAuthService = googleAuthService;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    @Override
    public UserDetailsResponseDto createUser(UserDetailsRequestDto userDetailsRequestDto, HttpServletRequest req) {
        User user = userDetailsRequestDto.toUser();
        String email = user.getEmail();
        String mobileNumber = user.getMobileNumber();
        if(userRepo.existsByEmail(email) ){
            throw new UserAlreadyExistsException(email);
        }
        if(userRepo.existsByMobileNumber(mobileNumber)){
            throw new UserAlreadyExistsException(mobileNumber);
        }
        user.setPassword(passwordEncoder.encode(userDetailsRequestDto.getPassword()));
        user = userRepo.save(user);
        UserDetailsResponseDto dto = UserDetailsResponseDto.fromUser(user);
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

    @Transactional
    public SessionToken createNewActiveSessionToken(User user, HttpServletRequest req){
        SessionToken sessionToken = new SessionToken();
        sessionToken.setUser(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        String token = tokenService.generateToken(authentication);
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

    @Override
    @Transactional
    public UserDetailsResponseDto processGoogleLogin(String token, HttpServletRequest req) {
        token = token.substring(7);
        GoogleIdToken.Payload payload = googleAuthService.verifyGoogleToken(token);
        if(payload == null){
            return null;
        } else {
            String email = payload.getEmail();
            User user = userRepo.findByEmail(email).orElse(null);
            if(user == null){
                user = new User();
                user.setEmail(email);
                user.setFirstName((String) payload.get("given_name"));
                user.setLastName((String) payload.get("family_name"));
                user.setRoles(List.of(new Role("USER")));
                user.setPassword(passwordEncoder.encode("GoogleOAuth2_0_Login"));
                user = userRepo.save(user);
            }
            SessionToken sessionToken = createNewActiveSessionToken(user, req);
            sessionTokenRepo.save(sessionToken);
            UserDetailsResponseDto dto = UserDetailsResponseDto.fromUser(user);
            dto.setToken(sessionToken.getToken());
            return dto;

        }
    }
}
