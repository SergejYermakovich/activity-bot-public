package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.entity.Category;
import com.github.xmlreader.activitybot.service.ActivityService;
import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoriesCommand implements BotCommand {
    
    private final MessageSender messageSender;
    private final ActivityService activityService;

    @Override
    public String getCommand() {
        return "/categories";
    }

    @Override
    public String getDescription() {
        return "Показать категории";
    }

    @Override
    public void execute(Long chatId) {
        List<Category> categories = activityService.getAllActiveCategories();
        
        if (categories.isEmpty()) {
            messageSender.sendMessage(chatId, "📂 Категории загружаются...");
            return;
        }

        StringBuilder text = new StringBuilder("📂 Категории активностей:\n\n");
        categories.forEach(category -> {
            String emoji = category.getEmoji() != null ? category.getEmoji() : "📌";
            text.append(emoji).append(" *").append(category.getName()).append("*\n");
            if (category.getDescription() != null) {
                text.append("   ").append(category.getDescription()).append("\n");
            }
            text.append("\n");
        });

        text.append("Используйте /search для просмотра всех активностей\n");

        messageSender.sendMessage(chatId, text.toString());
    }
}
