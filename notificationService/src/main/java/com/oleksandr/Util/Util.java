package com.oleksandr.Util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class Util {

    public Map<String, Object> getTicketModelFromProperties(Properties properties) {
        Map<String, Object> model = new HashMap<>();
        model.put("eventName", properties.getProperty("eventName"));
        model.put("ticketType", properties.getProperty("ticketType"));
        model.put("location", properties.getProperty("location"));
        model.put("ticketPrice", properties.getProperty("ticketPrice"));
        model.put("date", properties.getProperty("date"));

        return model;
    }
}
