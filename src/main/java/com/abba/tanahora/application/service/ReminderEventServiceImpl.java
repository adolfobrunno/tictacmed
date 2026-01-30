package com.abba.tanahora.application.service;

import com.abba.tanahora.domain.model.Reminder;
import com.abba.tanahora.domain.model.ReminderEvent;
import com.abba.tanahora.domain.model.ReminderEventStatus;
import com.abba.tanahora.domain.repository.ReminderEventRepository;
import com.abba.tanahora.domain.service.ReminderEventService;
import com.abba.tanahora.domain.service.ReminderService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class ReminderEventServiceImpl implements ReminderEventService {

    private final ReminderEventRepository reminderEventRepository;
    private final OpenAiApiService openAiApiService;
    private final ReminderService reminderService;

    public ReminderEventServiceImpl(ReminderEventRepository reminderEventRepository, OpenAiApiService openAiApiService, ReminderService reminderService) {
        this.reminderEventRepository = reminderEventRepository;
        this.openAiApiService = openAiApiService;
        this.reminderService = reminderService;
    }

    @Override
    public ReminderEvent registerDispatch(Reminder reminder, String whatsappMessageId) {
        ReminderEvent event = new ReminderEvent();
        event.setReminder(reminder);
        event.setWhatsappMessageId(whatsappMessageId);
        return reminderEventRepository.save(event);
    }

    @Override
    public Optional<ReminderEvent> findPendingByReminder(Reminder reminder) {
        return reminderEventRepository.findFirstByReminderAndStatusOrderBySentAtDesc(reminder, ReminderEventStatus.PENDING);
    }

    @Override
    public ReminderEvent updateDispatch(ReminderEvent event, String whatsappMessageId) {
        event.setWhatsappMessageId(whatsappMessageId);
        event.setSentAt(OffsetDateTime.now());
        return reminderEventRepository.save(event);
    }

    @Override
    public Optional<ReminderEvent> updateStatusFromResponse(String replyToMessageId, String responseText) {
        return Optional.empty();
    }

    @Override
    public Optional<ReminderEvent> updateLastPending(String userId, String responseText) {

        Optional<ReminderEventStatus> status = parseStatus(responseText);

        return reminderEventRepository.findFirstByReminderUserIdAndStatusOrderBySentAtDesc(userId, ReminderEventStatus.PENDING)
                .map(event -> {
                    event.setStatus(status.get());
                    event.setResponseReceivedAt(OffsetDateTime.now());
                    return reminderEventRepository.save(event);
                });
    }

    private Optional<ReminderEventStatus> parseStatus(String responseText) {
        if (responseText == null || responseText.isBlank()) {
            return Optional.empty();
        }

        ReminderEventStatusDTO reminderEventStatusDTO = openAiApiService.sendPrompt(String.format(
                """
                        Analise a seguinte mensagem e retorne um status adequado de acordo com o modelo.
                        
                        mensagem = %s
                        """, responseText), ReminderEventStatusDTO.class);

        return Optional.ofNullable(reminderEventStatusDTO.status);
    }

    static class ReminderEventStatusDTO {
        @JsonProperty(required = true)
        ReminderEventStatus status;
    }
}
