package com.oleksandr.kafka;

import com.oleksandr.common.notification.NotificationRequest;
import com.oleksandr.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final MailService mailService;

    @KafkaListener(topics = "mailNotifications", groupId = "mail_consumer")
    public void listen(NotificationRequest message){
        log.debug("Received mail request: Mail type {}, mail address: {} ",
                message.type(), message.userForMailDTO().mailAddress());

        mailService.sendEmail(message);
    }

    /*

        REGISTRATION_CONFIRM("Your successful registration at TicRes"),
        TICKET_PURCHASE("Congratulations on your successful purchase");


        register ms - registration confirm. after registration send message

        ticket_purchase - monolith, after ticket confirm
     */

}
