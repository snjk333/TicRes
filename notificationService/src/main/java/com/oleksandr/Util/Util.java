package com.oleksandr.Util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Util {

    public Map<String, Object> getTicketModelFromProperties(Map<String, String> properties) {
        Map<String, Object> model = new HashMap<>();
        model.put("eventName", properties.get("eventName"));
        model.put("ticketType", properties.get("ticketType"));
        model.put("location", properties.get("location"));
        model.put("ticketPrice", properties.get("ticketPrice"));
        model.put("date", properties.get("date"));

        return model;
    }
}
