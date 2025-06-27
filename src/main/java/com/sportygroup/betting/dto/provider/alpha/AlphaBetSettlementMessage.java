package com.sportygroup.betting.dto.provider.alpha;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sportygroup.betting.config.AlhpaOutcomeKeyDeserializer;
import com.sportygroup.betting.enums.AlhpaOutcome;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AlphaBetSettlementMessage extends ProviderAlphaMessage {
    @JsonDeserialize(keyUsing = AlhpaOutcomeKeyDeserializer.class)
    private AlhpaOutcome outcome;

    public void setOutcome(String outcome) {
        this.outcome = AlhpaOutcome.fromValue(outcome);
    }

    public AlhpaOutcome getOutcome() {
        return outcome;
    }
}
