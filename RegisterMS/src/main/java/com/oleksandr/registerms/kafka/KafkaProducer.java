package com.oleksandr.registerms.kafka;

import com.oleksandr.common.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    public Mono<Void> sendMessage(NotificationRequest message){
        kafkaTemplate.send("mailNotifications", message.userForMailDTO().mailAddress(), message);
        return null;
    }
}
