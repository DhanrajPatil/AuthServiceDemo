package com.elitefolk.authservicedemo.utils;

import com.elitefolk.authservicedemo.models.Role;
import com.elitefolk.authservicedemo.models.TokenData;
import com.elitefolk.authservicedemo.models.User;
import org.apache.commons.text.RandomStringGenerator;

import java.util.Base64;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

public class TokenGeneratorUtil {

    public static String generateTokenForUser(User user) {
        String token = RandomStringGenerator.builder()
                .withinRange('0', 'z')
                .filteredBy(LETTERS, DIGITS)
                .build()
                .generate(20);

        TokenData tokenData = new TokenData();
        tokenData.setToken(token);
        tokenData.setUserId(user.getId().toString());
        tokenData.setName(user.getFirstName() + " " + user.getLastName());
        tokenData.setEmail(user.getEmail());
        tokenData.setMobileNumber(user.getMobileNumber());
        tokenData.setExpiryDate(System.currentTimeMillis() + 180000);
        if(user.getRoles() != null) {
            for(Role role: user.getRoles()) {
                tokenData.getRoles().add(role.getName());
            }
        }
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(tokenData.toString().getBytes());
    }

    public static TokenData decodeToken(String token) {
        Base64.Decoder decoder = Base64.getDecoder();
        String decodedToken = new String(decoder.decode(token));
        String[] tokenData = decodedToken.split(",");
        TokenData tokenDataObj = new TokenData();
        String tokenValue = tokenData[0].split("=")[1];
        tokenDataObj.setToken(tokenValue);
        String userId = tokenData[1].split("=")[1];
        tokenDataObj.setUserId(userId);
        String name = tokenData[2].split("=")[1];
        tokenDataObj.setName(name);
        String email = tokenData[3].split("=")[1];
        tokenDataObj.setEmail(email);
        String mobileNumber = tokenData[4].split("=")[1];
        tokenDataObj.setMobileNumber(mobileNumber);
        String expiryDate = tokenData[5].split("=")[1];
        tokenDataObj.setExpiryDate(Long.parseLong(expiryDate));
        return tokenDataObj;
    }

    public static String generatePasswordResetToken() {
        return RandomStringGenerator.builder()
                .withinRange('0', 'z')
                .filteredBy(LETTERS, DIGITS)
                .build()
                .generate(20);
    }
}
