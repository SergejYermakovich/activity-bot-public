package com.github.xmlreader.activitybot.config;

import com.github.xmlreader.activitybot.bot.ActivityTelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TelegramBotConfig {

    private final ActivityTelegramBot activityTelegramBot;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(activityTelegramBot);
        log.info("ActivityTelegramBot registered successfully with token: {}", 
                botToken != null && botToken.length() > 10 
                    ? botToken.substring(0, 10) + "..." 
                    : "not configured");
        return botsApi;
    }
}
