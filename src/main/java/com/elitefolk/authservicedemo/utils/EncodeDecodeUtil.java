package com.elitefolk.authservicedemo.utils;

import java.util.Base64;

public class EncodeDecodeUtil {
    public static String encodeBase64(String value) {
        Base64.Encoder encoder = Base64.getEncoder();
        value = encoder.encodeToString(value.getBytes());
        return value;
    }

    public static String decodeBase64(String value) {
        byte[] pasBytes = Base64.getDecoder().decode(value);
        return new String(pasBytes);
    }
}
