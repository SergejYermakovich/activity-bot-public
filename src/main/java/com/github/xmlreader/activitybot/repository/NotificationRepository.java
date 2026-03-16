package com.github.xmlreader.activitybot.repository;

import com.github.xmlreader.activitybot.entity.Notification;
import com.github.xmlreader.activitybot.entity.Notification.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.scheduledTime <= :now")
    List<Notification> findByStatusAndScheduledTimeBefore(
        @Param("status") NotificationStatus status,
        @Param("now") LocalDateTime now
    );
    
    List<Notification> findByUserTelegramIdAndStatus(Long userTelegramId, NotificationStatus status);
    
    List<Notification> findByActivityIdAndStatus(Long activityId, NotificationStatus status);
    
    boolean existsByBookingIdAndNotificationTypeAndStatus(
        Long bookingId,
        Notification.NotificationType notificationType,
        NotificationStatus status
    );
}
