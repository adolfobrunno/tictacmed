package com.abba.tictacmed.infrastructure.messaging.whatsapp;

import com.abba.tictacmed.infrastructure.messaging.whatsapp.dto.MessageReceived;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.dto.MessageReceivedType;
import com.abba.tictacmed.infrastructure.openai.OpenAiApiHelper;
import com.abba.tictacmed.infrastructure.utils.Buttons;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class MessageReceivedResolver {

    private final OpenAiApiHelper openAiApiHelper;

    public MessageReceived readMessage(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }

        return Try.of(() -> readAsButton(message))
                .getOrElse(() -> readAsLLM(message));
    }

    private MessageReceived readAsButton(String message) {
        return Buttons.parseReply(message);
    }

    private MessageReceived readAsLLM(String message) {
        return openAiApiHelper.sendPrompt("""
                                    You are an expert in understanding user intents from short text messages.
                                    Given the following message, classify it into one of the following categories:
                                    %s.
                        
                                    Message: "%s"
                        
                                    If the messageType is REGISTER_NEW_MEDICATION, fill the others fields as well.
                                    If the end is not specified, assume it's 30 days from start.
                                    Remember that today is %s.
                                    The frequency is in seconds.
                        
                        """.formatted(Arrays.stream(MessageReceivedType.values()).map(MessageReceivedType::name).toList(),
                        message, ZonedDateTime.now()),
                MessageReceived.class);
    }

}
