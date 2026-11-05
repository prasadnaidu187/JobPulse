
package com.jobpulse.backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswordHash {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String password = "JobPulse@2026New";

        System.out.println(encoder.encode(password));
    }
}
