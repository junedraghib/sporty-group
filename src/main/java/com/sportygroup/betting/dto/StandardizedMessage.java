package com.sportygroup.betting.dto;

import com.sportygroup.betting.enums.MarketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class StandardizedMessage {
    private String eventId;
    private MarketType marketType = MarketType.MATCH_RESULT; //SET DEFAULT TO MATCH_RESULT
    private String timestamp = LocalDateTime.now().toString(); // Default to current time
    private String provider; // The provider of the message, e.g., "Alpha", "Beta"
}
