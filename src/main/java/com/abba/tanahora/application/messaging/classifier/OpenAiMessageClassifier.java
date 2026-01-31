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
                Voce é um parser de agendamentos de medicamentos que entende mensagens contendo medicamento, dose,
                frequencia e data de inicio e fim.
                
                Analise a seguinte mensagem:
                
                %s
                
                Retorne seguindo o padrão indicado.
                Para o campo 'dosage', informe a quantidade do medicamento a ser tomada,
                se nao houver essa informacao na mensagem, retorne 'não mencionado'.
                
                O type do retorno deve ser inferido de acordo com a mensagem recebida;
                Por exemplo:
                 - se a mensagem for uma saudação, o type deve ser WELCOME
                 - se a mensagem for um lembrete de medicamento, o type deve ser REMINDER_CREATION
                 - se a mensagem for uma resposta positiva de um lembrete de medicamento (tomei, ok, tudo certo, etc), o type deve ser REMINDER_RESPONSE_TAKEN
                 - se a mensagem for uma resposta negativa de um lembrete de medicamento (não tomei, não vou tomar, esqueci, etc), o type deve ser REMINDER_RESPONSE_SKIPPED
                 - se a mensagem for um cancelamento de um lembrete de medicamento, o type deve ser REMINDER_CANCEL
                 - se a mensagem for uma pergunta sobre quando é o próximo lembrete, o type deve ser CHECK_NEXT_DISPATCH
                 - se a mensagem for uma mensagem de suporte, o type deve ser SUPPORT
                 - se a mensagem for solicitando upgrade ou downgrade do plano, o type deve ser PLAN_UPGRADE ou PLAN_DOWNGRADE
                
                Quando a frequencia mencionar N vezes ao dia, crie uma frequencia ideal.
                Quando a frequencia mencionar 'após as refeições', utilize os horários 7:30, 13:00 e 20:00, repetindo todos os dias.
                
                """;
        return openAiApiService.sendPrompt(String.format(prompt, message.getBody()), AiMessageProcessorDto.class);
    }
}
