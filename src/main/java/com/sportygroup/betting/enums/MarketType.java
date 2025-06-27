package com.sportygroup.betting.enums;

public enum MarketType {
    MATCH_RESULT("1X2"),
    OVER_UNDER("OVER_UNDER"),
    HANDICAP("HANDICAP");

    private String marketType;
    MarketType(String marketType) {
        this.marketType = marketType;
    }
}
