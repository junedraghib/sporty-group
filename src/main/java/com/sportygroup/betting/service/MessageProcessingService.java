package com.sportygroup.betting.service;

import com.sportygroup.betting.adaptor.MessageAdapter;
import com.sportygroup.betting.dto.StandardizedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProcessingService {

    private final MessageAdapterFactory adapterFactory;
    private final InMemoryQueueService inMemoryQueueService;

    @Async("taskExecutor")
    public void processMessageAsync(Object providerMessage) {
        try {
            MessageAdapter adapter = adapterFactory.getAdapter(providerMessage);
            StandardizedMessage standardizedMessage = adapter.adapt(providerMessage);
            log.info("Processed message: {}", standardizedMessage);
            inMemoryQueueService.enqueue(standardizedMessage);
        } catch (Exception e) {
            log.error("Failed to process message: {}", providerMessage, e);
        }
    }
}