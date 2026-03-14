package com.github.xmlreader.activitybot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ActivityBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivityBotApplication.class, args);
    }
}
