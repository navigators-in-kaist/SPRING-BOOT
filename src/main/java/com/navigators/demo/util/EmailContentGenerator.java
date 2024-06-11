package com.navigators.demo.util;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class EmailContentGenerator {

    private final RandomStringGenerator randomStringGenerator = new RandomStringGenerator();

    public Map<String, String> makeVerifyCodeAndEmailContent() {
        Map<String, String> resultMap = new HashMap<>();

        String generatedRandom = randomStringGenerator.generate(5);
        String emailContent = "This is your verification code! \n\n" + generatedRandom;

        resultMap.put("code", generatedRandom);
        resultMap.put("content", emailContent);

        return resultMap;
    }

}
