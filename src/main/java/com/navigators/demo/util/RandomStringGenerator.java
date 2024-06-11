package com.navigators.demo.util;

import java.util.Random;

public class RandomStringGenerator {
    private static final String NUMERIC_CHARACTERS = "0123456789";
    private static final Random RANDOM = new Random();

    public static String generate(int length) {
        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(NUMERIC_CHARACTERS.length());
            result.append(NUMERIC_CHARACTERS.charAt(index));
        }

        return result.toString();
    }
}
