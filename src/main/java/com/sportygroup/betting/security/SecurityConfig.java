package com.sportygroup.betting.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "security")
@Getter
@Setter
public class SecurityConfig {
    private Checksum checksum = new Checksum();
    private Ratelimit ratelimit = new Ratelimit();

    @Getter
    @Setter
    public static class Checksum {
        private boolean enabled = false;
        private Map<String, String> secrets; // e.g., {"alpha": "secret-alpha", "beta": "secret-beta"}
    }

    @Getter
    @Setter
    public static class Ratelimit {
        private boolean enabled = false;
        private Map<String, Integer> limits; // e.g., {"alpha": 100, "beta": 150}
        private int periodSeconds = 60; // Default to 60 seconds
    }
}