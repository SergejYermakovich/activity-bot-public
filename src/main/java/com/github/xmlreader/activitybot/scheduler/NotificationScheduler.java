package com.github.xmlreader.activitybot.scheduler;

import com.github.xmlreader.activitybot.entity.Notification;
import com.github.xmlreader.activitybot.service.NotificationService;
import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    
    private final NotificationService notificationService;
    private final MessageSender messageSender;
    
    /**
     * Проверка и отправка уведомлений каждые 5 минут
     */
    @Scheduled(fixedRate = 300000) // 5 минут
    public void sendScheduledNotifications() {
        log.debug("Checking for pending notifications...");
        
        List<Notification> pendingNotifications = notificationService.getPendingNotifications();
        
        if (pendingNotifications.isEmpty()) {
            log.debug("No pending notifications");
            return;
        }
        
        log.info("Found {} pending notifications to send", pendingNotifications.size());
        
        for (Notification notification : pendingNotifications) {
            try {
                sendNotification(notification);
            } catch (Exception e) {
                log.error("Failed to send notification {}: {}", 
                         notification.getId(), e.getMessage(), e);
                notificationService.markAsFailed(notification.getId(), e.getMessage());
            }
        }
    }
    
    private void sendNotification(Notification notification) {
        Long chatId = notification.getUserTelegramId();
        String message = notification.getMessage();
        
        if (message == null || message.isBlank()) {
            message = buildDefaultMessage(notification);
        }
        
        log.info("Sending notification {} to user {}: {}", 
                notification.getId(), chatId, notification.getNotificationType());
        
        messageSender.sendMessage(chatId, message);
        
        notificationService.markAsSent(notification.getId());
        
        log.info("Notification {} sent successfully", notification.getId());
    }
    
    private String buildDefaultMessage(Notification notification) {
        Activity activity = notification.getActivity();
        String activityTitle = activity != null ? activity.getTitle() : "Активность";
        
        return switch (notification.getNotificationType()) {
            case REMINDER_24H -> 
                "🔔 Напоминание: Завтра \"" + activityTitle + "\"";
            case REMINDER_1H -> 
                "⏰ Напоминание: Через час \"" + activityTitle + "\"!";
            case BOOKING_CONFIRMATION -> 
                "✅ Вы записаны на \"" + activityTitle + "\"";
            case BOOKING_CANCELLED -> 
                "❌ Бронирование на \"" + activityTitle + "\" отменено";
            case WAITLIST_AVAILABLE -> 
                "🎉 Появилось место на \"" + activityTitle + "\"! Записаться?";
            default -> "Уведомление о \"" + activityTitle + "\"";
        };
    }
}
