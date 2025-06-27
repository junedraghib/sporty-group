package com.sportygroup.betting.service;

import com.sportygroup.betting.dto.StandardizedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
public class InMemoryQueueService {
    private final Queue<StandardizedMessage> queue = new ConcurrentLinkedQueue<>();
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public boolean enqueue(StandardizedMessage message) {
        if (processedEventIds.add(message.getEventId())) {
            queue.add(message);
            return true;
        }
        return false; // Duplicate event_id, skipped
    }

    public StandardizedMessage poll() {
        StandardizedMessage message = queue.poll();
        if (message != null) {
            processedEventIds.remove(message.getEventId());
        }
        return message;
    }

    public int size() {
        return queue.size();
    }

    public void printQueue() {
        log.info("Queue size: {}", queue.size());
        log.info("Queue: {}", queue);
    }

    protected Queue<StandardizedMessage> getQueue() {
        return queue;
    }
}