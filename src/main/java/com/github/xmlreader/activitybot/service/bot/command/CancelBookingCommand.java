package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.service.BookingService;
import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelBookingCommand implements BotCommand {
    
    private final MessageSender messageSender;
    private final BookingService bookingService;

    @Override
    public String getCommand() {
        return "/cancel_booking";
    }

    @Override
    public String getDescription() {
        return "Отменить бронирование";
    }

    @Override
    public void execute(Long chatId) {
        messageSender.sendMessage(chatId, 
            "❌ *Отмена бронирования*\n\n" +
            "Используйте: `/cancel_booking <ID бронирования>`\n\n" +
            "Пример: `/cancel_booking 123`\n\n" +
            "Чтобы узнать ID бронирования, используйте /my_bookings"
        );
    }
    
    public void executeWithId(Long chatId, Long bookingId) {
        try {
            bookingService.cancelBooking(bookingId, chatId);
            
            messageSender.sendMessage(chatId, 
                "✅ *Бронирование отменено!*\n\n" +
                "ID: #" + bookingId + "\n\n" +
                "Если это было ошибкой, вы можете записаться снова через /book"
            );
        } catch (com.github.xmlreader.activitybot.exception.ValidationException e) {
            messageSender.sendMessage(chatId, "❌ " + e.getMessage());
        } catch (Exception e) {
            messageSender.sendMessage(chatId, "❌ Произошла ошибка при отмене. Попробуйте позже.");
        }
    }
}
