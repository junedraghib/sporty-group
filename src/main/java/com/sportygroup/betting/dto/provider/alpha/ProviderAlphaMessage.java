package com.sportygroup.betting.dto.provider.alpha;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sportygroup.betting.enums.AlphaMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "msg_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AlphaOddsChangeMessage.class, name = "odds_update"),
        @JsonSubTypes.Type(value = AlphaBetSettlementMessage.class, name = "settlement")
})
public class ProviderAlphaMessage {
    @JsonProperty("msg_type")
    private AlphaMessageType messageType;
    @JsonProperty("event_id")
    private String eventId;
}
