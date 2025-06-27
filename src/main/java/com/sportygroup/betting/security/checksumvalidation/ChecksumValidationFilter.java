package com.sportygroup.betting.security.checksumvalidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.betting.exception.ChecksumValidationException;
import com.sportygroup.betting.exception.GlobalExceptionHandler;
import com.sportygroup.betting.security.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.stream.Collectors;

@Component
public class ChecksumValidationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(ChecksumValidationFilter.class);
    private final SecurityConfig securityConfig;

    public ChecksumValidationFilter(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, ChecksumValidationException {
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        String path = cachedRequest.getRequestURI();
        if (!path.matches("/api/v1/provider-(alpha|beta)/feed")) {
            filterChain.doFilter(cachedRequest, response);
            return;
        }

        String provider = extractProvider(path);
        if (securityConfig.getChecksum().isEnabled()) {
            String checksumHeader = cachedRequest.getHeader("X-Checksum");
            if (checksumHeader == null) {
                logger.warn("Missing X-Checksum header for provider: {}", provider);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType("application/json");

                GlobalExceptionHandler.ErrorResponse errorResponse = new GlobalExceptionHandler.ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Missing X-Checksum header",
                        "Please provide the X-Checksum header for provider: " + provider,
                        Instant.now().toString()
                );

                String json = new ObjectMapper().writeValueAsString(errorResponse);
                response.getWriter().write(json);
                return;
            }

            String secret = securityConfig.getChecksum().getSecrets().get(provider);
            if (secret == null) {
                logger.warn("No secret configured for provider: {}", provider);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType("application/json");

                GlobalExceptionHandler.ErrorResponse errorResponse = new GlobalExceptionHandler.ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid provider",
                        "Looks like this provider isn't configured, unable to found the secret for provider: " + provider,
                        Instant.now().toString()
                );

                String json = new ObjectMapper().writeValueAsString(errorResponse);
                response.getWriter().write(json);
                return;
            }

            String payload = cachedRequest.getReader().lines().collect(Collectors.joining());
            ObjectMapper objectMapper = new ObjectMapper();
            Object requestObject = objectMapper.readValue(payload, Object.class);
            payload = objectMapper.writeValueAsString(requestObject);
            String computedChecksum = HmacUtil.computeHmacSha256(payload, secret);

            if (!computedChecksum.equals(checksumHeader)) {
                logger.warn("Invalid checksum for provider: {}. Expected: {}, Received: {}", provider, computedChecksum, checksumHeader);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");

                GlobalExceptionHandler.ErrorResponse errorResponse = new GlobalExceptionHandler.ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Invalid checksum",
                        "Unable to verify the provider, invalid checksum: " + provider,
                        Instant.now().toString()
                );

                String json = new ObjectMapper().writeValueAsString(errorResponse);
                response.getWriter().write(json);
                return;
            }

            filterChain.doFilter(cachedRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String extractProvider(String path) {
        String[] parts = path.split("/");
        return parts.length >= 4 ? parts[3].replace("provider-", "") : null;
    }
}