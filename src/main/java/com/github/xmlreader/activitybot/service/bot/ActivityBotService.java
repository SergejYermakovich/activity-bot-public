package com.github.xmlreader.activitybot.service.bot;

import com.github.xmlreader.activitybot.config.BotConfig;
import com.github.xmlreader.activitybot.service.bot.handler.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;

@Slf4j
@Service
public class ActivityBotService implements LongPollingUpdateConsumer {
    
    private final BotConfig botConfig;
    private final UpdateHandler updateHandler;
    
    private BotSession botSession;
    private TelegramBotsLongPollingApplication botsApplication;

    public ActivityBotService(BotConfig botConfig, UpdateHandler updateHandler) {
        this.botConfig = botConfig;
        this.updateHandler = updateHandler;
    }

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            try {
                handleUpdate(update);
            } catch (Exception e) {
                log.error("Error processing update {}: {}", update.getUpdateId(), e.getMessage(), e);
            }
        }
    }

    private void handleUpdate(Update update) {
        if (update == null) {
            log.warn("Received null update, skipping");
            return;
        }
        updateHandler.handle(update);
    }

    @PostConstruct
    public void start() {
        botsApplication = new TelegramBotsLongPollingApplication();
        try {
            botSession = botsApplication.registerBot(botConfig.getBotToken(), this);
            log.info("Bot {} successfully started", botConfig.getBotName());
        } catch (Exception e) {
            log.error("Failed to start bot {}", botConfig.getBotName(), e);
            throw new RuntimeException("Failed to start bot", e);
        }
    }

    @PreDestroy
    public void stop() {
        try {
            if (botSession != null && botSession.isRunning()) {
                botSession.stop();
                log.info("Bot session stopped");
            }
            if (botsApplication != null) {
                botsApplication.close();
                log.info("Telegram application closed");
            }
        } catch (Exception e) {
            log.error("Error stopping bot", e);
        }
    }
}
