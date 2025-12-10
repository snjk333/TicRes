package com.oleksandr.service;

import com.oleksandr.common.notification.NotificationRequest;

public interface MailService {
    void sendEmail(NotificationRequest request);
}
