package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.entity.Activity;
import com.github.xmlreader.activitybot.entity.Booking;
import com.github.xmlreader.activitybot.service.ActivityService;
import com.github.xmlreader.activitybot.service.BookingService;
import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookCommand implements BotCommand {
    
    private final MessageSender messageSender;
    private final BookingService bookingService;
    private final ActivityService activityService;

    @Override
    public String getCommand() {
        return "/book";
    }

    @Override
    public String getDescription() {
        return "Записаться на активность";
    }

    @Override
    public void execute(Long chatId) {
        messageSender.sendMessage(chatId, 
            "📝 *Как записаться на активность:*\n\n" +
            "Используйте: `/book <ID активности>`\n\n" +
            "Пример: `/book 123`\n\n" +
            "Чтобы узнать ID активности, используйте /search\n\n" +
            "❓ Нужна помощь? Используйте /help"
        );
    }
    
    public void executeWithActivity(Long chatId, String userName, Long activityId) {
        try {
            Activity activity = activityService.getById(activityId);
            
            if (activity == null) {
                messageSender.sendMessage(chatId, 
                    "❌ Активность не найдена. Проверьте ID и попробуйте снова."
                );
                return;
            }
            
            String emoji = activity.getCategory() != null && activity.getCategory().getEmoji() != null 
                    ? activity.getCategory().getEmoji() : "📌";
            
            int availableSpots = activity.getAvailableSpots();
            
            String confirmationText = String.format("""
                %s *Подтверждение записи*
                
                📍 *%s*
                📂 %s
                🗺️ %s
                %s
                
                👥 Мест доступно: %d
                
                Вы хотите записаться?
                Да — просто отправьте любое сообщение
                Нет — напишите /cancel
                """,
                emoji,
                activity.getTitle(),
                activity.getCategory() != null ? activity.getCategory().getName() : "Без категории",
                activity.getLocation(),
                activity.getPrice() != null ? "💰 " + activity.getPrice() + " BYN" : "💰 Бесплатно",
                availableSpots
            );
            
            messageSender.sendMessage(chatId, confirmationText);
            
        } catch (Exception e) {
            messageSender.sendMessage(chatId, "❌ Произошла ошибка. Попробуйте позже.");
        }
    }
    
    public void confirmBooking(Long chatId, String userName, Long activityId) {
        try {
            var request = com.github.xmlreader.activitybot.dto.BookingRequest.builder()
                    .activityId(activityId)
                    .participantsCount(1)
                    .build();
            
            var response = bookingService.createBooking(request, chatId, userName);
            
            Activity activity = activityService.getById(activityId);
            String emoji = activity.getCategory() != null && activity.getCategory().getEmoji() != null 
                    ? activity.getCategory().getEmoji() : "📌";
            
            String successText = String.format("""
                ✅ *Вы успешно записаны!*
                
                📍 *%s*
                🎫 ID бронирования: #%d
                👥 Участников: %d
                %s
                
                📅 Начало: %s
                🗺️ Локация: %s
                
                Для отмены используйте: /cancel_booking %d
                """,
                activity.getTitle(),
                response.getId(),
                response.getParticipantsCount(),
                response.getTotalPrice() != null ? "💰 Стоимость: " + response.getTotalPrice() + " BYN" : "💰 Бесплатно",
                activity.getStartTime() != null ? activity.getStartTime().toLocalDate() + " " + activity.getStartTime().toLocalTime() : "Уточняется",
                activity.getLocation(),
                response.getId()
            );
            
            messageSender.sendMessage(chatId, successText);
            
        } catch (com.github.xmlreader.activitybot.exception.ValidationException e) {
            messageSender.sendMessage(chatId, "❌ " + e.getMessage());
        } catch (Exception e) {
            messageSender.sendMessage(chatId, "❌ Произошла ошибка при записи. Попробуйте позже.");
        }
    }
}
