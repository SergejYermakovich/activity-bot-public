package com.github.xmlreader.activitybot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_notification_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_telegram_id", nullable = false, unique = true)
    private Long userTelegramId;
    
    @Column(name = "remind_24h")
    @Builder.Default
    private Boolean remind24h = true;
    
    @Column(name = "remind_1h")
    @Builder.Default
    private Boolean remind1h = true;
    
    @Column(name = "remind_custom_hours")
    private Integer remindCustomHours;
    
    @Column(name = "email_notifications")
    @Builder.Default
    private Boolean emailNotifications = false;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
