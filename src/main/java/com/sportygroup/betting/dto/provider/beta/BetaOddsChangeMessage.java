package com.sportygroup.betting.dto.provider.beta;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sportygroup.betting.enums.BetaOutcome;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BetaOddsChangeMessage extends ProviderBetaMessage {
    @JsonProperty("odds")
    private Map<BetaOutcome, Double> odds;
}
