package com.sportygroup.betting.enums;

import lombok.Getter;

public enum AlhpaOutcome {
    ONE("1"), X("X"), TWO("2");

    @Getter
    private final String outcome;
    AlhpaOutcome(String outcome) {
        this.outcome = outcome;
    }

    public static AlhpaOutcome fromValue(String value) {
        for (AlhpaOutcome outcome : values()) {
            if (outcome.outcome.equals(value)) {
                return outcome;
            }
        }
        throw new IllegalArgumentException("Invalid Alpha outcome: " + value);
    }
}
