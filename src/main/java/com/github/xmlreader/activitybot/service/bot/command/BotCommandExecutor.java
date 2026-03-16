package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.service.ActivityService;
import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotCommandExecutor {
    
    private final MessageSender messageSender;
    private final ActivityService activityService;
    private final Map<String, BotCommand> commands;

    public void execute(Long chatId, String command) {
        BotCommand botCommand = commands.get(command);
        
        if (botCommand == null) {
            messageSender.sendMessage(chatId, "Неизвестная команда. Используйте /help для справки.");
            return;
        }

        try {
            botCommand.execute(chatId);
        } catch (Exception e) {
            log.error("Error executing command {}: {}", command, e.getMessage(), e);
            messageSender.sendMessage(chatId, "Произошла ошибка при выполнении команды. Попробуйте позже.");
        }
    }
}
