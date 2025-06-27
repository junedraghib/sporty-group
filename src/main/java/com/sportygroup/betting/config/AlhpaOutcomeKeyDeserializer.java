package com.sportygroup.betting.config;

import com.fasterxml.jackson.databind.KeyDeserializer;
import com.sportygroup.betting.enums.AlhpaOutcome;

import java.io.IOException;

public class AlhpaOutcomeKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws IOException {
        for (AlhpaOutcome outcome : AlhpaOutcome.values()) {
            if (outcome.getOutcome().equals(key)) {
                return outcome;
            }
        }
        throw new IllegalArgumentException("Invalid key for Alhpa Outcome: " + key);
    }
}