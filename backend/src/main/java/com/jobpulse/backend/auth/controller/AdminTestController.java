
package com.jobpulse.backend.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminTestController {

    @GetMapping("/api/v1/admin-test")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> adminTest() {
        return Map.of(
                "message", "Welcome Admin! You have ADMIN access."
        );
    }
}
