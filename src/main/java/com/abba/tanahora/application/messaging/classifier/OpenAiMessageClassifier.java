package com.abba.tanahora.application.messaging.classifier;

import com.abba.tanahora.application.dto.AiMessageProcessorDto;
import com.abba.tanahora.application.messaging.AIMessage;
import com.abba.tanahora.application.messaging.flow.FlowState;
import com.abba.tanahora.application.service.OpenAiApiService;
import org.springframework.stereotype.Component;

@Component
public class OpenAiMessageClassifier implements MessageClassifier {

    private static final String CLASSIFICATION_KEY = "classification";

    private final OpenAiApiService openAiApiService;

    public OpenAiMessageClassifier(OpenAiApiService openAiApiService) {
        this.openAiApiService = openAiApiService;
    }

    @Override
    public AiMessageProcessorDto classify(AIMessage message, FlowState state) {

        Object cached = state.getContext().get(CLASSIFICATION_KEY);
        if (cached instanceof AiMessageProcessorDto) {
            return (AiMessageProcessorDto) cached;
        }
        AiMessageProcessorDto dto = this.iaClassify(message);
        if (dto != null) {
            state.getContext().put(CLASSIFICATION_KEY, dto);
        }
        return dto;
    }

    private AiMessageProcessorDto iaClassify(AIMessage message) {
        String prompt = """
                Voce e um assistente de agendamentos de medicamentos.
                
                Analise a seguinte mensagem:
                
                %s
                
                Retorne seguindo o padrao indicado.
                """;
        return openAiApiService.sendPrompt(String.format(prompt, message.getBody()), AiMessageProcessorDto.class);
    }
}
