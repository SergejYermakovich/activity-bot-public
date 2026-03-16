package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.service.NotificationService;
import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetReminderCommand implements BotCommand {
    
    private final MessageSender messageSender;
    private final NotificationService notificationService;

    @Override
    public String getCommand() {
        return "/set_reminder";
    }

    @Override
    public String getDescription() {
        return "Изменить настройки напоминаний";
    }

    @Override
    public void execute(Long chatId) {
        String helpText = """
            ⚙️ *Изменение настроек напоминаний*
            
            Использование:
            /set_reminder <тип> <вкл/выкл>
            
            Типы:
            • 24h — напоминание за 24 часа
            • 1h — напоминание за 1 час
            
            Примеры:
            /set_reminder 24h вкл
            /set_reminder 24h выкл
            /set_reminder 1h вкл
            /set_reminder 1h выкл
            
            ---
            Проверить настройки: /notifications
            """;
        
        messageSender.sendMessage(chatId, helpText);
    }
    
    public void executeWithArgs(Long chatId, String args) {
        if (args == null || args.isBlank()) {
            execute(chatId);
            return;
        }
        
        String[] parts = args.trim().split("\\s+");
        if (parts.length != 2) {
            messageSender.sendMessage(chatId, 
                "❌ Неверный формат.\n\n" +
                "Используйте: /set_reminder <тип> <вкл/выкл>\n\n" +
                "Пример: /set_reminder 24h вкл"
            );
            return;
        }
        
        String type = parts[0].toLowerCase();
        String action = parts[1].toLowerCase();
        
        if (!type.equals("24h") && !type.equals("1h")) {
            messageSender.sendMessage(chatId, 
                "❌ Неверный тип.\n\n" +
                "Допустимые типы: 24h, 1h"
            );
            return;
        }
        
        boolean enable;
        if (action.equals("вкл") || action.equals("on") || action.equals("true")) {
            enable = true;
        } else if (action.equals("выкл") || action.equals("off") || action.equals("false")) {
            enable = false;
        } else {
            messageSender.sendMessage(chatId, 
                "❌ Неверное значение.\n\n" +
                "Используйте: вкл, выкл, on, off"
            );
            return;
        }
        
        try {
            if (type.equals("24h")) {
                notificationService.updateSettings(chatId, enable, null, null);
                messageSender.sendMessage(chatId, 
                    "✅ Напоминание за 24 часа " + (enable ? "включено" : "выключено")
                );
            } else if (type.equals("1h")) {
                notificationService.updateSettings(chatId, null, enable, null);
                messageSender.sendMessage(chatId, 
                    "✅ Напоминание за 1 час " + (enable ? "включено" : "выключено")
                );
            }
        } catch (Exception e) {
            messageSender.sendMessage(chatId, 
                "❌ Ошибка при обновлении настроек: " + e.getMessage()
            );
        }
    }
}
