package com.sportygroup.betting.adaptor;

import com.sportygroup.betting.dto.StandardizedBetSettlementMessage;
import com.sportygroup.betting.dto.StandardizedMessage;
import com.sportygroup.betting.dto.StandardizedOddsChangeMessage;
import com.sportygroup.betting.dto.provider.alpha.AlphaBetSettlementMessage;
import com.sportygroup.betting.dto.provider.alpha.AlphaOddsChangeMessage;
import com.sportygroup.betting.enums.AlhpaOutcome;
import com.sportygroup.betting.enums.MarketType;
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
public class AlphaMessageAdapter implements MessageAdapter {

    @Override
    public StandardizedMessage adapt(Object providerMessage) {
        if (providerMessage instanceof AlphaOddsChangeMessage) {
            return adaptOddsChange((AlphaOddsChangeMessage) providerMessage);
        } else if (providerMessage instanceof AlphaBetSettlementMessage) {
            return adaptBetSettlement((AlphaBetSettlementMessage) providerMessage);
        }
        throw new IllegalArgumentException("Unsupported Alpha message type: " + providerMessage.getClass().getName());
    }

    private StandardizedOddsChangeMessage adaptOddsChange(AlphaOddsChangeMessage message) {
        List<StandardizedOddsChangeMessage.StandardOdds> standardOdds = message.getValues().entrySet().stream()
                .map(entry -> StandardizedOddsChangeMessage.StandardOdds.builder()
                        .outcome(mapAlphaToStandardOutcome(entry.getKey()))
                        .value(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        return StandardizedOddsChangeMessage.builder()
                .eventId(message.getEventId())
                .marketType(MarketType.MATCH_RESULT)
                .timestamp(LocalDateTime.now())
                .odds(standardOdds)
                .build();
    }

    private StandardizedBetSettlementMessage adaptBetSettlement(AlphaBetSettlementMessage message) {
        StandardizedBetSettlementMessage.StandardSettlement standardSettlement = new StandardizedBetSettlementMessage.StandardSettlement();
        standardSettlement.setOutcome(mapAlphaToStandardOutcome(message.getOutcome()));
        standardSettlement.setResult("WIN"); // Assuming result is the outcome name

        return StandardizedBetSettlementMessage.builder()
                .eventId(message.getEventId())
                .marketType(MarketType.MATCH_RESULT)
                .timestamp(LocalDateTime.now())
                .outcomes(Collections.singletonList(standardSettlement))
                .build();
    }

    private StandardOutcome mapAlphaToStandardOutcome(AlhpaOutcome alphaOutcome) {
        switch (alphaOutcome) {
            case ONE:
                return StandardOutcome.HOME_WIN;
            case X:
                return StandardOutcome.DRAW;
            case TWO:
                return StandardOutcome.AWAY_WIN;
            default:
                throw new IllegalArgumentException("Unsupported Alpha outcome: " + alphaOutcome);
        }
    }
}