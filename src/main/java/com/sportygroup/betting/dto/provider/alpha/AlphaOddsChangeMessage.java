package com.sportygroup.betting.dto.provider.alpha;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sportygroup.betting.config.AlhpaOutcomeKeyDeserializer;
import com.sportygroup.betting.enums.AlhpaOutcome;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlphaOddsChangeMessage extends ProviderAlphaMessage {
    @JsonProperty("values")
    @JsonDeserialize(keyUsing = AlhpaOutcomeKeyDeserializer.class)
    private Map<AlhpaOutcome, Double> values;
}
