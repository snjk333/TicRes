package com.oleksandr.registerms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RegisterMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegisterMsApplication.class, args);
    }

}
