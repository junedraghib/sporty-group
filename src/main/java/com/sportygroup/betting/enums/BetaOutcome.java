package com.sportygroup.betting.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BetaOutcome {
    HOME("home"), DRAW("draw"), AWAY("away");
    private final String outcome;

    BetaOutcome(String outcome) {
        this.outcome = outcome;
    }

    @JsonValue
    public String getValue() {
        return outcome;
    }

    @JsonCreator
    public static BetaOutcome fromValue(String value) {
        for (BetaOutcome betaOutcome : values()) {
            if (betaOutcome.outcome.equalsIgnoreCase(value)) {
                return betaOutcome;
            }
        }
        throw new IllegalArgumentException("Invalid BetaOutcome value: " + value);
    }
}
