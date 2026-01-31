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
                Você é um parser especializado em instruções de medicamentos para lembretes automáticos.
                
                Regras obrigatórias:
                1. Se não há data explícita → dataInicio = hoje, dataFim = em um ano
                2. COUNT deve ser calculado também até a data fim
                3. "a cada X horas" → FREQ=DAILY; COUNT calculado pelos dias
                4. "todos os dias" → FREQ=DAILY
                5. "todas as manhãs" → FREQ=DAILY;BYHOUR=08;BYMINUTE=00; horario="08:00"
                6. "durante X dias" → COUNT=X dias a partir de hoje
                7. Nome deve ser o principal ativo/princípio (ex: "dipirona", não "comprimido")
                8. Dosagem vazia → "1 dose ou comprimido"
                
                Exemplos de entrada/saída:
                
                "tomar um comprimido de dipirona a cada 8 horas durante 5 dias"
                → {"medication": "dipirona", "dosage": "1 comprimido", "rrule": "FREQ=DAILY;COUNT=15", "startDate": "2026-01-30", "type": "REMINDER_CREATION"}
                
                "desogestrel 20:30 todos os dias"
                → {"medication": "desogestrel", "dosage": "1 comprimido", "rrule": "FREQ=DAILY;BYHOUR=20;BYMINUTE=30;", "startDate": "2026-01-30", "type": "REMINDER_CREATION"}
                
                "Vitamina D todas as manhãs"
                → {"medication": "Vitamina D", "dosage": "1 dose", "rrule": "FREQ=DAILY;BYHOUR=07;BYMINUTE=30;", "startDate": "2026-01-30", "type": "REMINDER_CREATION"}
                
                Agora analise esta mensagem e retorne APENAS o JSON:
                
                %s
                """;
        return openAiApiService.sendPrompt(String.format(prompt, message.getBody()), AiMessageProcessorDto.class);
    }
}
