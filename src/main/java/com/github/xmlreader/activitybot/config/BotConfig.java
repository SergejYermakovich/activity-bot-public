package com.github.xmlreader.activitybot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class BotConfig {
    
    @Value("${telegram.bot.username}")
    private String botName;

    @Value("${telegram.bot.token}")
    private String botToken;
}
