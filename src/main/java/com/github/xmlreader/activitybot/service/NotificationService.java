package com.github.xmlreader.activitybot.service;

import com.github.xmlreader.activitybot.entity.*;
import com.github.xmlreader.activitybot.repository.NotificationRepository;
import com.github.xmlreader.activitybot.repository.UserNotificationSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserNotificationSettingsRepository settingsRepository;
    
    public UserNotificationSettings getUserSettings(Long userTelegramId) {
        return settingsRepository.findByUserTelegramId(userTelegramId)
                .orElseGet(() -> {
                    UserNotificationSettings newSettings = UserNotificationSettings.builder()
                            .userTelegramId(userTelegramId)
                            .remind24h(true)
                            .remind1h(true)
                            .build();
                    return settingsRepository.save(newSettings);
                });
    }
    
    @Transactional
    public UserNotificationSettings updateSettings(Long userTelegramId, 
                                                    Boolean remind24h,
                                                    Boolean remind1h,
                                                    Integer remindCustomHours) {
        UserNotificationSettings settings = getUserSettings(userTelegramId);
        
        if (remind24h != null) {
            settings.setRemind24h(remind24h);
        }
        if (remind1h != null) {
            settings.setRemind1h(remind1h);
        }
        if (remindCustomHours != null) {
            settings.setRemindCustomHours(remindCustomHours);
        }
        
        return settingsRepository.save(settings);
    }
    
    @Transactional
    public Notification scheduleNotification(Long userTelegramId,
                                              Activity activity,
                                              Booking booking,
                                              Notification.NotificationType type,
                                              LocalDateTime scheduledTime,
                                              String message) {
        
        // Проверяем, не запланировано ли уже такое уведомление
        boolean alreadyScheduled = notificationRepository.existsByBookingIdAndNotificationTypeAndStatus(
            booking != null ? booking.getId() : null,
            type,
            Notification.NotificationStatus.SCHEDULED
        );
        
        if (alreadyScheduled) {
            log.debug("Notification already scheduled for booking {}, type {}", 
                     booking != null ? booking.getId() : "N/A", type);
            return null;
        }
        
        Notification notification = Notification.builder()
                .userTelegramId(userTelegramId)
                .activity(activity)
                .booking(booking)
                .notificationType(type)
                .scheduledTime(scheduledTime)
                .status(Notification.NotificationStatus.SCHEDULED)
                .message(message)
                .build();
        
        Notification saved = notificationRepository.save(notification);
        log.info("Scheduled notification {} for user {} at {}", 
                saved.getId(), userTelegramId, scheduledTime);
        
        return saved;
    }
    
    @Transactional
    public void scheduleBookingReminders(Booking booking) {
        Activity activity = booking.getActivity();
        LocalDateTime activityStart = activity.getStartTime();
        
        if (activityStart == null) {
            log.warn("Activity {} has no start time, skipping reminders", activity.getId());
            return;
        }
        
        UserNotificationSettings settings = getUserSettings(booking.getUserTelegramId());
        
        // Напоминание за 24 часа
        if (settings.getRemind24h()) {
            LocalDateTime reminder24h = activityStart.minusHours(24);
            if (reminder24h.isAfter(LocalDateTime.now())) {
                String message24h = String.format(
                    "🔔 Напоминание: Завтра \"%s\" в %s\n📍 %s",
                    activity.getTitle(),
                    activityStart.toLocalTime(),
                    activity.getLocation()
                );
                scheduleNotification(
                    booking.getUserTelegramId(),
                    activity,
                    booking,
                    Notification.NotificationType.REMINDER_24H,
                    reminder24h,
                    message24h
                );
            }
        }
        
        // Напоминание за 1 час
        if (settings.getRemind1h()) {
            LocalDateTime reminder1h = activityStart.minusHours(1);
            if (reminder1h.isAfter(LocalDateTime.now())) {
                String message1h = String.format(
                    "⏰ Напоминание: Через час \"%s\"!\n📍 %s\nНе опаздывай!",
                    activity.getTitle(),
                    activity.getLocation()
                );
                scheduleNotification(
                    booking.getUserTelegramId(),
                    activity,
                    booking,
                    Notification.NotificationType.REMINDER_1H,
                    reminder1h,
                    message1h
                );
            }
        }
        
        log.info("Scheduled reminders for booking {} on activity {}", 
                booking.getId(), activity.getTitle());
    }
    
    public List<Notification> getPendingNotifications() {
        return notificationRepository.findByStatusAndScheduledTimeBefore(
            Notification.NotificationStatus.SCHEDULED,
            LocalDateTime.now()
        );
    }
    
    @Transactional
    public void markAsSent(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));
        
        notification.setStatus(Notification.NotificationStatus.SENT);
        notification.setSentTime(LocalDateTime.now());
        notificationRepository.save(notification);
        
        log.debug("Notification {} marked as sent", notificationId);
    }
    
    @Transactional
    public void markAsFailed(Long notificationId, String errorMessage) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));
        
        notification.setStatus(Notification.NotificationStatus.FAILED);
        notification.setErrorMessage(errorMessage);
        notificationRepository.save(notification);
        
        log.error("Notification {} failed: {}", notificationId, errorMessage);
    }
    
    @Transactional
    public void cancelNotificationsForBooking(Long bookingId) {
        List<Notification> notifications = notificationRepository.findByUserTelegramIdAndStatus(
            bookingId, Notification.NotificationStatus.SCHEDULED
        );
        
        notifications.forEach(n -> {
            n.setStatus(Notification.NotificationStatus.CANCELLED);
            notificationRepository.save(n);
        });
        
        log.info("Cancelled {} notifications for booking {}", notifications.size(), bookingId);
    }
}
