package com.github.xmlreader.activitybot.service.bot.handler;

import com.github.xmlreader.activitybot.service.bot.exception.UpdateProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class ActivityUpdateHandler implements UpdateHandler {
    
    private final TextMessageHandler textMessageHandler;

    public ActivityUpdateHandler(TextMessageHandler textMessageHandler) {
        this.textMessageHandler = textMessageHandler;
    }

    @Override
    public void handle(Update update) {
        try {
            if (update == null) {
                log.warn("Received null update");
                return;
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                textMessageHandler.handle(update);
            } else {
                log.debug("Unsupported update type: {}", update);
            }
        } catch (Exception e) {
            log.error("Error processing update {}: {}", update.getUpdateId(), e.getMessage(), e);
            throw new UpdateProcessingException("Failed to process update", e);
        }
    }
}
