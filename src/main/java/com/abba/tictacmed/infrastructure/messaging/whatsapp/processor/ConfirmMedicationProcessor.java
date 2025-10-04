package com.abba.tictacmed.infrastructure.messaging.whatsapp.processor;

import com.abba.tictacmed.application.scheduling.command.ConfirmMedicationCommand;
import com.abba.tictacmed.application.scheduling.usecases.ConfirmMedicationUseCase;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.WhatsAppClient;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.dto.MessageContext;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.dto.MessageReceivedType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmMedicationProcessor implements SimpleProcessor {

    private final ConfirmMedicationUseCase confirmMedicationUseCase;
    private final WhatsAppClient whatsAppClient;

    @Override
    public MessageReceivedType resolveType() {
        return MessageReceivedType.CONFIRM_MEDICATION;
    }

    @Override
    public void process(MessageContext context) {

        log.info("Confirming medication from message: {}", context);

        confirmMedicationUseCase.execute(new ConfirmMedicationCommand(
                context.contactNumber(), context.messageReceived().medicine(), true
        ));

        String body = "Ok, registrei aqui. Muito obrigado e até a próxima.";

        whatsAppClient.sendText(context.contactNumber(), body);

    }
}
