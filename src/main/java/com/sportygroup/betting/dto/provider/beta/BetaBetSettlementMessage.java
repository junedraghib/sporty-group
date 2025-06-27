package com.sportygroup.betting.dto.provider.beta;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportygroup.betting.enums.BetaOutcome;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BetaBetSettlementMessage extends ProviderBetaMessage {
    @JsonProperty("result")
    private BetaOutcome result;
}
