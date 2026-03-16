package com.github.xmlreader.activitybot.service.bot.handler;

import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import com.github.xmlreader.activitybot.service.bot.command.BotCommandExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class TextMessageHandler implements UpdateHandler {
    
    private final MessageSender messageSender;
    private final BotCommandExecutor commandExecutor;

    public TextMessageHandler(MessageSender messageSender, BotCommandExecutor commandExecutor) {
        this.messageSender = messageSender;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String userName = getUserName(update);

        log.debug("Received message from {}: {}", chatId, text);

        if (text.startsWith("/")) {
            commandExecutor.execute(chatId, userName, text);
        }
    }
    
    private String getUserName(Update update) {
        if (update.getMessage().getFrom() != null) {
            String firstName = update.getMessage().getFrom().getFirstName();
            String lastName = update.getMessage().getFrom().getLastName();
            if (lastName != null) {
                return firstName + " " + lastName;
            }
            return firstName;
        }
        return "User";
    }
}
