package com.sportygroup.betting.adaptor;

import com.sportygroup.betting.dto.StandardizedBetSettlementMessage;
import com.sportygroup.betting.dto.StandardizedMessage;
import com.sportygroup.betting.dto.StandardizedOddsChangeMessage;
import com.sportygroup.betting.dto.provider.beta.BetaBetSettlementMessage;
import com.sportygroup.betting.dto.provider.beta.BetaOddsChangeMessage;
import com.sportygroup.betting.enums.BetaOutcome;
import com.sportygroup.betting.enums.MarketType;
import com.sportygroup.betting.enums.MessageType;
import com.sportygroup.betting.enums.StandardOutcome;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BetaMessageAdapter implements MessageAdapter {

    @Override
    public StandardizedMessage adapt(Object providerMessage) {
        if (providerMessage instanceof BetaOddsChangeMessage) {
            return adaptOddsChange((BetaOddsChangeMessage) providerMessage);
        } else if (providerMessage instanceof BetaBetSettlementMessage) {
            return adaptBetSettlement((BetaBetSettlementMessage) providerMessage);
        }
        throw new IllegalArgumentException("Unsupported Beta message type: " + providerMessage.getClass().getName());
    }

    private StandardizedOddsChangeMessage adaptOddsChange(BetaOddsChangeMessage message) {
        List<StandardizedOddsChangeMessage.StandardOdds> standardOdds = message.getOdds().entrySet().stream()
                .map(entry -> StandardizedOddsChangeMessage.StandardOdds.builder()
                        .outcome(mapBetaToStandardOutcome(entry.getKey()))
                        .value(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        return StandardizedOddsChangeMessage.builder()
                .eventId(message.getEventId())
                .marketType(MarketType.MATCH_RESULT)
                .provider("Beta")
                .messageType(MessageType.ODDS_UPDATE)
                .timestamp(LocalDateTime.now().toString())
                .odds(standardOdds)
                .build();
    }

    private StandardizedBetSettlementMessage adaptBetSettlement(BetaBetSettlementMessage message) {
        StandardizedBetSettlementMessage.StandardSettlement standardSettlement = new StandardizedBetSettlementMessage.StandardSettlement();
        standardSettlement.setOutcome(mapBetaToStandardOutcome(message.getResult()));
        standardSettlement.setResult("WIN"); // Assuming result is the outcome name

        return StandardizedBetSettlementMessage.builder()
                .eventId(message.getEventId())
                .marketType(MarketType.MATCH_RESULT)
                .timestamp(LocalDateTime.now().toString())
                .messageType(MessageType.BET_SETTLEMENT)
                .provider("Beta")
                .outcomes(Collections.singletonList(standardSettlement))
                .build();
    }

    private StandardOutcome mapBetaToStandardOutcome(BetaOutcome betaOutcome) {
        switch (betaOutcome) {
            case HOME:
                return StandardOutcome.HOME_WIN;
            case DRAW:
                return StandardOutcome.DRAW;
            case AWAY:
                return StandardOutcome.AWAY_WIN;
            default:
                throw new IllegalArgumentException("Unsupported Alpha outcome: " + betaOutcome);
        }
    }
}