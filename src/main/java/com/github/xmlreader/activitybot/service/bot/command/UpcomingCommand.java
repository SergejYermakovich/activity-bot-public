package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.entity.Activity;
import com.github.xmlreader.activitybot.entity.Category;
import com.github.xmlreader.activitybot.service.ActivityService;
import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UpcomingCommand implements BotCommand {
    
    private final MessageSender messageSender;
    private final ActivityService activityService;

    @Override
    public String getCommand() {
        return "/upcoming";
    }

    @Override
    public String getDescription() {
        return "Показать предстоящие события";
    }

    @Override
    public void execute(Long chatId) {
        List<Activity> activities = activityService.getAllActiveActivities();
        
        List<Activity> upcoming = activities.stream()
                .filter(a -> a.getStartTime() != null && a.getStartTime().isAfter(LocalDateTime.now()))
                .limit(5)
                .toList();
        
        if (upcoming.isEmpty()) {
            messageSender.sendMessage(chatId, "📅 Пока нет предстоящих активностей. Следите за обновлениями!");
            return;
        }

        StringBuilder text = new StringBuilder("📅 Предстоящие активности:\n\n");
        upcoming.forEach(activity -> {
            Category category = activity.getCategory();
            String emoji = category != null ? category.getEmoji() : "📌";
            
            text.append(emoji).append(" *").append(activity.getTitle()).append("*\n");
            text.append("🕐 ").append(activity.getStartTime()).append("\n");
            if (category != null) {
                text.append("📂 ").append(category.getName()).append("\n");
            }
            if (activity.getLocation() != null) {
                text.append("🗺️ ").append(activity.getLocation()).append("\n");
            }
            text.append("\n");
        });

        messageSender.sendMessage(chatId, text.toString());
    }
}
