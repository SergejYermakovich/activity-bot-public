package com.github.xmlreader.activitybot.repository;

import com.github.xmlreader.activitybot.entity.UserNotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserNotificationSettingsRepository extends JpaRepository<UserNotificationSettings, Long> {
    
    Optional<UserNotificationSettings> findByUserTelegramId(Long userTelegramId);
    
    boolean existsByUserTelegramId(Long userTelegramId);
}
