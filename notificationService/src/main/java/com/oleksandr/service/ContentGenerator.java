package com.oleksandr.service;

import com.oleksandr.Util.Util;
import com.oleksandr.common.notification.UserForMailDTO;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ContentGenerator {


    private final Configuration configuration;
    private final Util utility;

    public String getRegistrationEmailContent(UserForMailDTO userForMailDTO, Map<String, String> properties) {
        StringWriter writer = new StringWriter();
        Map<String, Object> model = new HashMap<>();

        model.put("nameOfClient", userForMailDTO.name());
        model.put("title", properties.get("title"));

        try {
            configuration.getTemplate("register.ftlh")
                    .process(model, writer);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return writer.getBuffer().toString();
    }


    public String getTicketEmailContent(UserForMailDTO userForMailDTO, Map<String, String> properties) {

        StringWriter writer = new StringWriter();

        Map<String, Object> model = utility.getTicketModelFromProperties(properties);

        model.put("nameOfClient", userForMailDTO.name());




        try {
            configuration.getTemplate("ticket.ftlh")
                    .process(model, writer);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return writer.getBuffer().toString();

    }
}
