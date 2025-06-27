package com.sportygroup.betting.service;

import com.sportygroup.betting.adaptor.AlphaMessageAdapter;
import com.sportygroup.betting.adaptor.BetaMessageAdapter;
import com.sportygroup.betting.adaptor.MessageAdapter;
import com.sportygroup.betting.dto.provider.alpha.ProviderAlphaMessage;
import com.sportygroup.betting.dto.provider.beta.ProviderBetaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageAdapterFactory {

    private final List<MessageAdapter> adapters;

    public MessageAdapter getAdapter(Object providerMessage) {
        for (MessageAdapter adapter : adapters) {
            if (canAdapt(adapter, providerMessage)) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("No adapter found for message type: " + providerMessage.getClass().getName());
    }

    private boolean canAdapt(MessageAdapter adapter, Object providerMessage) {
        return (adapter instanceof AlphaMessageAdapter && providerMessage instanceof ProviderAlphaMessage) ||
               (adapter instanceof BetaMessageAdapter && providerMessage instanceof ProviderBetaMessage);
    }
}