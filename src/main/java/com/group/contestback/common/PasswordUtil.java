package com.group.contestback.common;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;

import java.security.SecureRandom;

public class PasswordUtil {
    public static String generateRandomPassword() {
        SecureRandom rng = new SecureRandom();

        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange(0, 'Z')
                .filteredBy(CharacterPredicates.ASCII_ALPHA_NUMERALS)
                .usingRandom(rng::nextInt)
                .build();

        return generator.generate(10);
    }
}
