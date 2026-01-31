package com.abba.tanahora.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiApiService {

    private final OpenAiChatModel openAiChatModel;

    public <T> T sendPrompt(String stringPrompt, Class<T> schema) {

        log.debug("Sending prompt to OpenAI: {}", stringPrompt);

        var converter = new BeanOutputConverter<>(schema);
        String jsonSchema = converter.getJsonSchema();

        var prompt = new Prompt(
                stringPrompt,
                OpenAiChatOptions.builder()
                        .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                        .build()
        );

        var response = openAiChatModel.call(prompt);

        String responseText = response.getResult().getOutput().getText();

        log.debug("Response: {}", responseText);

        return converter.convert(Objects.requireNonNull(responseText));
    }


}

