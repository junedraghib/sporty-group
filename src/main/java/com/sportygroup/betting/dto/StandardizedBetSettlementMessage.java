package com.sportygroup.betting.dto;

import com.sportygroup.betting.enums.StandardOutcome;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class StandardizedBetSettlementMessage extends StandardizedMessage {
    private List<StandardSettlement> outcomes;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StandardSettlement{
        private StandardOutcome outcome;
        private String result;
    }
}
