package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import com.github.xmlreader.activitybot.service.bot.state.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelCreateCommand implements BotCommand {
    
    private final MessageSender messageSender;
    private final UserStateService userStateService;

    @Override
    public String getCommand() {
        return "/cancel_create";
    }

    @Override
    public String getDescription() {
        return "Отменить создание активности";
    }

    @Override
    public void execute(Long chatId) {
        if (!userStateService.hasActiveCreation(chatId)) {
            messageSender.sendMessage(chatId, 
                "ℹ️ У вас нет активного процесса создания активности."
            );
            return;
        }
        
        userStateService.clearState(chatId);
        
        messageSender.sendMessage(chatId, 
            "❌ *Создание активности отменено!*\n\n" +
            "Все введённые данные удалены.\n\n" +
            "Начать заново: /create"
        );
    }
}
