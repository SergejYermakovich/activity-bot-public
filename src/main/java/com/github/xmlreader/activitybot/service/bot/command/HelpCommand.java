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
                
                Этот бот помогает находить интересные активности в городе и записываться на них.
                
                📋 *Основные команды:*
                /start - Начать работу с ботом
                /search - Показать все активности
                /categories - Показать категории
                /upcoming - Предстоящие события
                
                🎫 *Бронирование:*
                /book <ID> - Записаться на активность
                /my_bookings - Мои бронирования
                /cancel_booking <ID> - Отменить бронирование
                
                📝 *Организаторам:*
                /create - Создать новую активность
                /cancel_create - Отменить создание
                
                ❓ *Другое:*
                /help - Эта справка
                
                💡 *Советы:*
                - Используйте /search чтобы найти активности
                - Для записи используйте /book <ID активности>
                - Проверить свои записи можно в /my_bookings
                - Отменить запись можно через /cancel_booking
                
                Есть вопросы? Свяжитесь с разработчиком @xmlreader
                """;
        messageSender.sendMessage(chatId, text);
    }
}
