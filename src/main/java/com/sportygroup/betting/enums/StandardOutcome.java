package com.sportygroup.betting.enums;

public enum StandardOutcome {
    HOME_WIN("home_win"), DRAW("draw"), AWAY_WIN("away_win");

    private String outcome;
    StandardOutcome(String outcome) {
        this.outcome = outcome;
    }
}
