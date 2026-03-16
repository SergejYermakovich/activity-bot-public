package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelpCommand implements BotCommand {
    
    private final MessageSender messageSender;

    @Override
    public String getCommand() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "Показать справку";
    }

    @Override
    public void execute(Long chatId) {
        String text = """
                ℹ️ Помощь по Activity Bot
                
                Этот бот помогает находить интересные активности в городе.
                
                📋 Команды:
                /start - Начать работу с ботом
                /search - Показать все активности
                /categories - Показать категории
                /upcoming - Предстоящие события
                /help - Эта справка
                
                💡 Советы:
                - Регулярно проверяйте бота для новых активностей
                - Следите за предстоящими событиями в /upcoming
                - Используйте /categories чтобы узнать категории
                
                Есть вопросы? Свяжитесь с разработчиком @xmlreader
                """;
        messageSender.sendMessage(chatId, text);
    }
}
