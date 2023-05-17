package com.group.contestback.common;

import org.apache.commons.text.RandomStringGenerator;

public class LoginUtil {
    public static String generateRandomLogin() {

        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .selectFrom(chars.toCharArray())
                .build();

        return generator.generate(8);
    }

}
