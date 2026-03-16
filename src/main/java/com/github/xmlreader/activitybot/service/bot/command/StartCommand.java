package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartCommand implements BotCommand {
    
    private final MessageSender messageSender;

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public String getDescription() {
        return "Начать работу с ботом";
    }

    @Override
    public void execute(Long chatId) {
        String text = """
                🎯 Добро пожаловать в Activity Bot!
                
                Я помогу найти интересные активности в вашем городе:
                • Квесты и escape rooms
                • Футбольные матчи
                • Концерты и выставки
                • Мастер-классы
                • И многое другое!
                
                📋 Доступные команды:
                /search - Поиск активностей
                /categories - Категории активностей
                /upcoming - Предстоящие события
                /help - Помощь
                
                Начните с /search чтобы найти что-то интересное!
                """;
        messageSender.sendMessage(chatId, text);
    }
}
