package com.sportygroup.betting.dto;

import com.sportygroup.betting.enums.StandardOutcome;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class StandardizedOddsChangeMessage extends StandardizedMessage {
    private List<StandardOdds> odds;

    @Data
    @Builder
    public static class StandardOdds {
        private StandardOutcome outcome;
        private double value;
    }
}
