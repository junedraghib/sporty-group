package com.sportygroup.betting.controller;

import com.sportygroup.betting.dto.RestApiResponse;
import com.sportygroup.betting.dto.provider.alpha.ProviderAlphaMessage;
import com.sportygroup.betting.dto.provider.beta.ProviderBetaMessage;
import com.sportygroup.betting.service.MessageProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProviderMessageController {

    private final MessageProcessingService processingService;

    @PostMapping("/v1/provider-alpha/feed")
    public ResponseEntity<RestApiResponse> receiveAlphaMessage(@RequestBody ProviderAlphaMessage alphaMessage) {
        try {
            //processing messages asynchronously, in order to release the thread quickly for next webhook to receive
            processingService.processMessageAsync(alphaMessage);
            log.info("Received message from Provider Alpha: {}", alphaMessage);
            return ResponseEntity.ok(
                    RestApiResponse.builder().status("200").message("Message received and queued for processing").build()
            );
        } catch (IllegalArgumentException e) {
            log.error("Invalid message format: {}", alphaMessage, e);
            return ResponseEntity.badRequest().body(
                    RestApiResponse.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .message("Invalid message format")
                            .error(e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/v1/provider-beta/feed")
    public ResponseEntity<RestApiResponse> receiveBetaMessage(@RequestBody ProviderBetaMessage betaMessage) {
        try {
            //processing messages asynchronously, in order to release the thread quickly for next webhook to receive
            processingService.processMessageAsync(betaMessage);
            return ResponseEntity.ok(
                    RestApiResponse.builder().status("200").message("Message received and queued for processing").build()
            );
        } catch (IllegalArgumentException e) {
            log.error("Invalid message format: {}", betaMessage, e);
            return ResponseEntity.badRequest().body(
                    RestApiResponse.builder()
                            .status(HttpStatus.BAD_REQUEST.toString())
                            .message("Invalid message format")
                            .error(e.getMessage())
                            .build()
            );
        }
    }
}