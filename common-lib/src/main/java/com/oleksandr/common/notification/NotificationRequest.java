package com.oleksandr.common.notification;

import com.oleksandr.common.enums.MAIL_TYPE;

import java.util.Properties;

public record NotificationRequest(
    UserForMailDTO userForMailDTO,
    MAIL_TYPE type,
    Properties properties
) { }
