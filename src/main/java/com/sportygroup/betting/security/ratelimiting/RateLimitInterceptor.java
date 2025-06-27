package com.sportygroup.betting.security.ratelimiting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.betting.exception.GlobalExceptionHandler;
import com.sportygroup.betting.exception.RateLimitExceededException;
import com.sportygroup.betting.security.SecurityConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);
    private final SecurityConfig securityConfig;
    private final Map<String, TokenBucket> rateLimiters = new ConcurrentHashMap<>();

    public RateLimitInterceptor(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        if (!path.matches("/api/v1/provider-(alpha|beta)/feed")) {
            return true;
        }

        String provider = extractProvider(path);
        if (securityConfig.getRatelimit().isEnabled()) {
            Integer limit = securityConfig.getRatelimit().getLimits().getOrDefault(provider, 100);
            TokenBucket bucket = rateLimiters.computeIfAbsent(provider,
                p -> new TokenBucket(limit, (double) limit / securityConfig.getRatelimit().getPeriodSeconds()));
            if (!bucket.tryConsume()) {
                logger.warn("Rate limit exceeded for provider: {}", provider);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                try {
                    GlobalExceptionHandler.ErrorResponse errorResponse = new GlobalExceptionHandler.ErrorResponse(
                            HttpStatus.TOO_MANY_REQUESTS.value(),
                            "Rate limit exceeded",
                            "Rate limit exceeded for provider: " + provider + " please try after " + securityConfig.getRatelimit().getPeriodSeconds() + " seconds",
                            Instant.now().toString()
                    );

                    String json = new ObjectMapper().writeValueAsString(errorResponse);
                    response.getWriter().write(json);
                } catch (IOException e) {
                    logger.error("Couldn't write response IO exception: {}", e.getMessage());
                }

                return false;
            }
            logger.debug("Rate limit check passed for provider: {}. Remaining tokens: {}", provider, bucket.getTokens());
        }
        return true;
    }

    private String extractProvider(String path) {
        String[] parts = path.split("/");
        return parts.length >= 4 ? parts[3].replace("provider-", "") : null;
    }
}