package com.abba.tictacmed.infrastructure.messaging.whatsapp.processor;

import com.abba.tictacmed.infrastructure.messaging.whatsapp.dto.MessageContext;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.dto.MessageReceivedType;

public interface SimpleProcessor {

    MessageReceivedType resolveType();

    void process(MessageContext context);

}
