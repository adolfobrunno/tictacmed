package com.abba.tanahora.application.messaging.handler;

import com.abba.tanahora.application.dto.AiMessageProcessorDto;
import com.abba.tanahora.application.dto.MessageReceivedType;
import com.abba.tanahora.application.messaging.AIMessage;
import com.abba.tanahora.application.messaging.classifier.MessageClassifier;
import com.abba.tanahora.application.messaging.flow.FlowState;
import com.abba.tanahora.application.service.OpenAiApiService;
import com.abba.tanahora.domain.model.User;
import com.abba.tanahora.domain.service.NotificationService;
import com.abba.tanahora.domain.service.UserService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(900)
public class WelcomeMessageHandler implements MessageHandler {

    private final MessageClassifier messageClassifier;
    private final NotificationService notificationService;
    private final OpenAiApiService openAiApiService;
    private final UserService userService;

    public WelcomeMessageHandler(MessageClassifier messageClassifier, NotificationService notificationService, OpenAiApiService openAiApiService, UserService userService) {
        this.messageClassifier = messageClassifier;
        this.notificationService = notificationService;
        this.openAiApiService = openAiApiService;
        this.userService = userService;
    }

    @Override
    public boolean supports(AIMessage message, FlowState state) {
        AiMessageProcessorDto dto = messageClassifier.classify(message, state);
        return dto.getType() == MessageReceivedType.WELCOME;
    }

    @Override
    public void handle(AIMessage message, FlowState state) {

        User user = userService.register(message.getWhatsappId(), message.getContactName());

        String prompt = """
                Você é um assistente virtual do ´Tá na Hora!´ que agenda medicamentos a serem tomados.
                Analise a mensagem do usuário e responda com uma mensagem de boas-vindas.
                
                Mensagem = %s
                
                Seja cordial, gentil e breve na sua resposta.
                Ensine ele a criar um novo lembrete de medicamento.
                Para criar um novo lembrete ele deve informar o nome do medicamento, a quantidade a ser tomada, a frequência e a data de início.
                
                Informe também sobre os dois planos disponíveis:
                - Plano gratuito: 1 lembrete ativo por tempo indeterminado
                - Plano premium: lembretes ilimitados por R$ 9,99 ao mês (em breve)
                """;

        WelcomeMessageDTO welcomeMessageDTO = openAiApiService.sendPrompt(String.format(prompt, message.getBody()), WelcomeMessageDTO.class);

        notificationService.sendNotification(user, welcomeMessageDTO.message);

        state.setCurrentFlow(null);
        state.setStep(null);
        state.getContext().clear();

    }

    static class WelcomeMessageDTO {
        @JsonProperty(required = true)
        String message;
    }
}
