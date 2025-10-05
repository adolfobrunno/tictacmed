package com.abba.tictacmed.infrastructure.messaging.whatsapp.processor;

import com.abba.tictacmed.application.patient.command.RegisterPatientCommand;
import com.abba.tictacmed.application.patient.command.RegisterPatientResult;
import com.abba.tictacmed.application.patient.usecases.RegisterPatientUseCase;
import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleCommand;
import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleResult;
import com.abba.tictacmed.application.scheduling.usecases.CreateMedicationScheduleUseCase;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.WhatsAppClient;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.dto.MessageContext;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.dto.MessageReceivedType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewScheduleProcessor implements SimpleProcessor {

    private final RegisterPatientUseCase registerPatientUseCase;
    private final CreateMedicationScheduleUseCase createNewMedicationScheduleUseCase;
    private final WhatsAppClient whatsAppClient;

    @Override
    public MessageReceivedType resolveType() {
        return MessageReceivedType.REGISTER_NEW_MEDICATION;
    }

    @Override
    public void process(MessageContext context) {

        log.info("Creating new schedule from message: {}", context);

        RegisterPatientResult newPatient = registerPatientUseCase.execute(new RegisterPatientCommand(
                context.contactName(),
                context.contactNumber(),
                true
        ));

        CreateMedicationScheduleResult createMedicationScheduleResult = createNewMedicationScheduleUseCase.execute(
                new CreateMedicationScheduleCommand(
                        newPatient.contact(),
                        context.messageReceived().medicine(),
                        context.messageReceived().start(),
                        context.messageReceived().end(),
                        Duration.ofSeconds(context.messageReceived().frequency()),
                        context.messageReceived().recurring()
                ));

        whatsAppClient.sendText(context.contactNumber(), """
                Oi, %s. Seu novo agendamento foi criado com sucesso.
                Você receberá uma notificação quando for o momento de administrar o medicamento.
                
                Até lá.
                """.formatted(context.contactName()));

        log.info("New schedule created: {}", createMedicationScheduleResult);

    }
}
