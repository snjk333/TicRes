package com.oleksandr.service;

import com.oleksandr.common.notification.NotificationRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;
    private final ContentGenerator contentGenerator;

    @Override
    public void sendEmail(NotificationRequest request) {
        switch (request.type()) {
            case REGISTRATION_CONFIRM -> sendRegistrationEmail(request);
            case TICKET_PURCHASE -> sendTicketEmail(request);
            default -> throw new IllegalArgumentException("WTF");
        }
    }

    private void sendRegistrationEmail(NotificationRequest request) {
        String emailContent = contentGenerator.getRegistrationEmailContent(request.userForMailDTO(), request.properties());
        SendStandartMimeMessage(request, emailContent);
    }

    private void sendTicketEmail(NotificationRequest request) {
        String emailContent = contentGenerator.getTicketEmailContent(request.userForMailDTO(), request.properties());
        SendStandartMimeMessage(request, emailContent);
    }

    private void SendStandartMimeMessage(NotificationRequest request, String emailContent) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setSubject(request.type().getTitle());
            helper.setTo(request.userForMailDTO().mailAddress());
            helper.setFrom(from);


            helper.setText(emailContent, true); // html content

            mailSender.send(mimeMessage);
        }
        catch (MessagingException exception){
            log.error("Exception " + exception.getMessage());
        }
    }
}
