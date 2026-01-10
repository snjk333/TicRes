package com.oleksandr.registerms.kafka;

import com.oleksandr.common.enums.MAIL_TYPE;
import com.oleksandr.common.notification.NotificationRequest;
import com.oleksandr.common.notification.UserForMailDTO;
import com.oleksandr.registerms.entity.users.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EmailMapper {
    public NotificationRequest buildRegistrationNotificationRequest(User savedUser){
        UserForMailDTO userForMail = new UserForMailDTO(
                savedUser.getFirstName(),
                savedUser.getEmail()
        );
        MAIL_TYPE type = MAIL_TYPE.REGISTRATION_CONFIRM;
        Map<String, String> properties = new HashMap<>();
        properties.put("title", "TicRes registration");

        return new NotificationRequest(
                userForMail,
                type,
                properties
        );
    }
}
