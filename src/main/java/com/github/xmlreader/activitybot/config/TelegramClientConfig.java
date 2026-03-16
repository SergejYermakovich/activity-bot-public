package com.github.xmlreader.activitybot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

@Configuration
public class TelegramClientConfig {
    
    @Bean
    public OkHttpTelegramClient telegramClient() {
        return new OkHttpTelegramClient();
    }
}
