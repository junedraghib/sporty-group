package com.sportygroup.betting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sportygroup.betting.dto.StandardizedMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class QueueWatcherService {

    private final InMemoryQueueService queueService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ObjectMapper objectMapper;
    public QueueWatcherService(InMemoryQueueService queueService) {
        this.queueService = queueService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty printing
    }

    @PostConstruct
    public void startWatching() {
        scheduler.scheduleAtFixedRate(this::watchQueue, 0, 1, TimeUnit.SECONDS);
    }

    private void watchQueue() {
        Queue<StandardizedMessage> queue = queueService.getQueue();
        StandardizedMessage message;
        while ((message = queue.poll()) != null) {
            try {
                log.info("New message received in standardized queue");
                log.info(objectMapper.writeValueAsString(message));
            } catch (Exception e) {
                log.error("Error processing message from queue: {}", message, e);
            }
        }
    }
}