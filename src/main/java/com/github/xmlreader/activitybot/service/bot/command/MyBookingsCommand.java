package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.dto.BookingResponse;
import com.github.xmlreader.activitybot.service.BookingService;
import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MyBookingsCommand implements BotCommand {
    
    private final MessageSender messageSender;
    private final BookingService bookingService;

    @Override
    public String getCommand() {
        return "/my_bookings";
    }

    @Override
    public String getDescription() {
        return "Мои бронирования";
    }

    @Override
    public void execute(Long chatId) {
        List<BookingResponse> bookings = bookingService.getUserActiveBookings(chatId);
        
        if (bookings.isEmpty()) {
            messageSender.sendMessage(chatId, 
                "📋 У вас пока нет активных бронирований.\n\n" +
                "Используйте /search чтобы найти активности и /book чтобы записаться!"
            );
            return;
        }
        
        StringBuilder text = new StringBuilder("📋 *Мои бронирования:*\n\n");
        
        bookings.forEach(booking -> {
            text.append("🎫 *#").append(booking.getId()).append("* — ").append(booking.getActivityTitle()).append("\n");
            if (booking.getActivityStartTime() != null) {
                text.append("📅 ").append(booking.getActivityStartTime().toLocalDate())
                    .append(" в ").append(booking.getActivityStartTime().toLocalTime()).append("\n");
            }
            text.append("👥 Участников: ").append(booking.getParticipantsCount()).append("\n");
            if (booking.getTotalPrice() != null) {
                text.append("💰 Стоимость: ").append(booking.getTotalPrice()).append(" BYN\n");
            }
            text.append("✅ Статус: ").append(getStatusEmoji(booking.getStatus()))
                .append(" ").append(booking.getStatus()).append("\n\n");
        });
        
        text.append("Для отмены используйте: /cancel_booking <ID>\n");
        
        messageSender.sendMessage(chatId, text.toString());
    }
    
    private String getStatusEmoji(com.github.xmlreader.activitybot.entity.Booking.BookingStatus status) {
        return switch (status) {
            case CONFIRMED -> "✅";
            case CANCELLED -> "❌";
            case COMPLETED -> "✔️";
            case WAITLIST -> "⏳";
        };
    }
}
