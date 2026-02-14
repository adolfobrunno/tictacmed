package com.abba.tanahora.application.messaging.handler;

import com.abba.tanahora.application.dto.AiMessageProcessorDto;
import com.abba.tanahora.application.dto.MessageReceivedType;
import com.abba.tanahora.application.messaging.AIMessage;
import com.abba.tanahora.application.messaging.classifier.MessageClassifier;
import com.abba.tanahora.application.messaging.flow.FlowState;
import com.abba.tanahora.application.notification.BasicWhatsAppMessage;
import com.abba.tanahora.domain.model.User;
import com.abba.tanahora.domain.service.NotificationService;
import com.abba.tanahora.domain.service.UserService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(500)
public class PlanUpgradeHandler implements HandleAndFlushMessageHandler {

    private static final String PLAN_UPGRADE_URI = "https://tanahora.app/checkout";

    private final UserService userService;
    private final MessageClassifier messageClassifier;
    private final NotificationService notificationService;

    public PlanUpgradeHandler(UserService userService, MessageClassifier messageClassifier, NotificationService notificationService) {
        this.userService = userService;
        this.messageClassifier = messageClassifier;
        this.notificationService = notificationService;
    }

    @Override
    public boolean supports(AIMessage message, FlowState state) {
        AiMessageProcessorDto classify = messageClassifier.classify(message, state);
        return classify.getType() == MessageReceivedType.PLAN_UPGRADE;
    }

    @Override
    public void handleAndFlush(AIMessage message, FlowState state) {

        String userId = state.getUserId();
        User user = userService.findByWhatsappId(userId);

        notificationService.sendNotification(user,
                BasicWhatsAppMessage.builder()
                        .to(user.getWhatsappId())
                        .message(String.format(
                                """
                                        Oi, que bom que está gostando.
                                        Por favor, conclua seu upgrade em %s
                                        
                                        Assim que concluir te mando uma confirmação por aqui. 🚀
                                        """, PLAN_UPGRADE_URI
                        ))
                        .build());
    }
}
