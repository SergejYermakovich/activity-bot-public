package com.github.xmlreader.activitybot.service.bot.command;

public interface BotCommand {
    String getCommand();
    String getDescription();
    void execute(Long chatId);
}
