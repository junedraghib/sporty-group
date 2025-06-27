package com.sportygroup.betting.adaptor;

import com.sportygroup.betting.dto.StandardizedMessage;

public interface MessageAdapter {
    StandardizedMessage adapt(Object providerMessage);
}