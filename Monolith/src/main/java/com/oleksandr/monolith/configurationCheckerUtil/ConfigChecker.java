package com.oleksandr.monolith.configurationCheckerUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConfigChecker implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ConfigChecker.class);

    @Value("${payu.client.id}")
    private String payuClientId;

    @Value("${payu.client.secret}")
    private String payuClientSecret;

    @Override
    public void run(String... args) throws Exception {
        log.info("========================================================================");
        log.info("               CHECKING APPLICATION CONFIGURATION                     ");
        log.info("========================================================================");

        if (payuClientId != null && !payuClientId.isEmpty()) {
            log.info("✅ PayU Client ID successfully loaded: {}", payuClientId);
        } else {
            log.error("❌ PayU Client ID is NOT loaded. Check your .env file or configuration.");
        }

        if (payuClientSecret != null && !payuClientSecret.isEmpty()) {
            // В реальном проекте никогда не выводи полный секрет!
            // Для отладки покажем только первые несколько символов.
            log.info("✅ PayU Client Secret successfully loaded. Starts with: {}", payuClientSecret.substring(0, Math.min(payuClientSecret.length(), 4)) + "...");
        } else {
            log.error("❌ PayU Client Secret is NOT loaded. Check your .env file or configuration.");
        }

        log.info("========================================================================");
    }
}
