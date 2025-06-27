package com.sportygroup.betting.dto.provider.beta;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sportygroup.betting.enums.BetaMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BetaOddsChangeMessage.class, name = "ODDS"),
        @JsonSubTypes.Type(value = BetaBetSettlementMessage.class, name = "SETTLEMENT")
})
public class ProviderBetaMessage {
    @JsonProperty("type")
    private BetaMessageType messageType;
    @JsonProperty("event_id")
    private String eventId;
}
