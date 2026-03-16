package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.entity.UserNotificationSettings;
import com.github.xmlreader.activitybot.service.NotificationService;
import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationsCommand implements BotCommand {
    
    private final MessageSender messageSender;
    private final NotificationService notificationService;

    @Override
    public String getCommand() {
        return "/notifications";
    }

    @Override
    public String getDescription() {
        return "Настройки уведомлений";
    }

    @Override
    public void execute(Long chatId) {
        UserNotificationSettings settings = notificationService.getUserSettings(chatId);
        
        String text = String.format("""
            ⚙️ *Настройки уведомлений*
            
            🔔 *Напоминания о событиях:*
            • За 24 часа: %s
            • За 1 час: %s
            
            ---
            💡 *Управление:*
            /set_reminder 24h [вкл/выкл] — напоминание за 24 часа
            /set_reminder 1h [вкл/выкл] — напоминание за 1 час
            
            Пример:
            /set_reminder 24h вкл
            /set_reminder 1h выкл
            
            ---
            ℹ️ Напоминания приходят автоматически при записи на активность
            """,
            settings.getRemind24h() ? "✅ Включено" : "❌ Выключено",
            settings.getRemind1h() ? "✅ Включено" : "❌ Выключено"
        );
        
        messageSender.sendMessage(chatId, text);
    }
}
