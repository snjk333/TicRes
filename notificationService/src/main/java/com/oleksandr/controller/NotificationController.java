package com.oleksandr.controller;

import com.oleksandr.common.notification.NotificationRequest;
import com.oleksandr.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final MailService mailService;

    @Deprecated // used for tests

    @PostMapping("/mail")
    public ResponseEntity<Void> sendMailNotification(NotificationRequest request) {
        mailService.sendEmail(request);
        return ResponseEntity.ok(null);
    }

}
