package com.github.xmlreader.activitybot.service.bot.handler;

import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import com.github.xmlreader.activitybot.service.bot.command.BotCommandExecutor;
import com.github.xmlreader.activitybot.service.bot.state.UserStateService;
import com.github.xmlreader.activitybot.service.bot.state.ActivityCreationState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class TextMessageHandler implements UpdateHandler {
    
    private final MessageSender messageSender;
    private final BotCommandExecutor commandExecutor;
    private final ActivityCreationHandler activityCreationHandler;
    private final UserStateService userStateService;

    public TextMessageHandler(MessageSender messageSender, 
                             BotCommandExecutor commandExecutor,
                             ActivityCreationHandler activityCreationHandler,
                             UserStateService userStateService) {
        this.messageSender = messageSender;
        this.commandExecutor = commandExecutor;
        this.activityCreationHandler = activityCreationHandler;
        this.userStateService = userStateService;
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

        // Проверяем, находится ли пользователь в процессе создания активности
        if (userStateService.hasActiveCreation(chatId)) {
            activityCreationHandler.handleTextMessage(chatId, userName, text);
            return;
        }

        // Обычная обработка команд
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
