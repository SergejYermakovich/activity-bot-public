package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.entity.Activity;
import com.github.xmlreader.activitybot.entity.Category;
import com.github.xmlreader.activitybot.service.ActivityService;
import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchCommand implements BotCommand {
    
    private final MessageSender messageSender;
    private final ActivityService activityService;

    @Override
    public String getCommand() {
        return "/search";
    }

    @Override
    public String getDescription() {
        return "Показать все активности";
    }

    @Override
    public void execute(Long chatId) {
        List<Activity> activities = activityService.getAllActiveActivities();
        
        if (activities.isEmpty()) {
            messageSender.sendMessage(chatId, "😔 Пока нет доступных активностей. Загляните позже!");
            return;
        }

        StringBuilder text = new StringBuilder("🎪 Доступные активности:\n\n");
        activities.stream().limit(10).forEach(activity -> {
            Category category = activity.getCategory();
            String emoji = category != null ? category.getEmoji() : "📌";
            
            text.append(emoji).append(" *").append(activity.getTitle()).append("*\n");
            if (category != null) {
                text.append("📂 ").append(category.getName()).append("\n");
            }
            if (activity.getLocation() != null) {
                text.append("🗺️ ").append(activity.getLocation()).append("\n");
            }
            if (activity.getPrice() != null) {
                text.append("💰 ").append(activity.getPrice()).append(" BYN\n");
            }
            if (activity.getStartTime() != null) {
                text.append("🕐 ").append(activity.getStartTime().toLocalDate()).append("\n");
            }
            
            // Show available spots
            int availableSpots = activity.getAvailableSpots();
            if (activity.getMaxParticipants() != null) {
                if (availableSpots <= 0) {
                    text.append("❌ Мест нет\n");
                } else if (availableSpots <= 3) {
                    text.append("🔥 Осталось мест: ").append(availableSpots).append("\n");
                } else {
                    text.append("👥 Мест: ").append(availableSpots).append("\n");
                }
            }
            
            text.append("➡️ Записаться: /book ").append(activity.getId()).append("\n\n");
        });

        if (activities.size() > 10) {
            text.append("... и ещё ").append(activities.size() - 10).append(" активностей\n");
        }

        messageSender.sendMessage(chatId, text.toString());
    }
}
