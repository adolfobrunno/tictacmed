package com.abba.tictacmed.infrastructure.messaging.whatsapp.processor;

import com.abba.tictacmed.application.scheduling.command.ConfirmMedicationCommand;
import com.abba.tictacmed.application.scheduling.usecases.ConfirmMedicationUseCase;
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

    @Override
    public MessageReceivedType resolveType() {
        return MessageReceivedType.CONFIRM_MEDICATION;
    }

    @Override
    public void process(MessageContext context) {

        log.info("Confirming medication from message: {}", context);

        confirmMedicationUseCase.execute(new ConfirmMedicationCommand(
                context.contactName(), context.messageReceived().medicine(), true
        ));

    }
}
