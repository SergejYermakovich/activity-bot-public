package com.github.xmlreader.activitybot.service.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {
    void handle(Update update);
}
