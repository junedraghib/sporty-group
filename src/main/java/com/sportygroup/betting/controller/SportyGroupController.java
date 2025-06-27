package com.sportygroup.betting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.betting.dto.RestApiResponse;
import com.sportygroup.betting.security.SecurityConfig;
import com.sportygroup.betting.security.checksumvalidation.HmacUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class SportyGroupController {
    private final SecurityConfig securityConfig;

    public SportyGroupController(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    @PostMapping("/api/v1/internal/checksum/{provider}")
    public ResponseEntity<RestApiResponse> checksum(@PathVariable String provider, @RequestBody Object request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String payload = objectMapper.writeValueAsString(request);

            if( provider == null || provider.isEmpty() || !provider.matches("alpha|beta")) {
                return ResponseEntity.badRequest().body(RestApiResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.toString())
                        .message("Invalid provider")
                        .error("Provider must be 'alpha' or 'beta'")
                        .build());
            }
            String secret = securityConfig.getChecksum().getSecrets().get(provider);
            String computedChecksum = HmacUtil.computeHmacSha256(payload, secret);
            return ResponseEntity.ok(RestApiResponse.builder()
                    .status(HttpStatus.OK.toString())
                    .message("Checksum computed successfully")
                    .data(Map.of("checksum", computedChecksum))
                    .build());
        } catch (Exception e) {
            log.error("Checksum computation failed for provider {}: {}", provider, e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    RestApiResponse.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .message("Checksum computation failed")
                            .error(e.getMessage())
                            .build()
            );
        }
    }
}
