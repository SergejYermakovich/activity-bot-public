package com.github.xmlreader.activitybot.bot;

import com.github.xmlreader.activitybot.entity.Activity;
import com.github.xmlreader.activitybot.entity.Category;
import com.github.xmlreader.activitybot.service.ActivityService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityTelegramBot extends TelegramLongPollingBot {

    private final ActivityService activityService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @PostConstruct
    public void init() {
        log.info("ActivityTelegramBot initialized with username: {}", botUsername);
        // Инициализируем категории при старте
        activityService.initializeDefaultCategories();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            log.debug("Received message from {}: {}", chatId, messageText);

            switch (messageText) {
                case "/start" -> handleStart(chatId);
                case "/search" -> handleSearch(chatId);
                case "/categories" -> handleCategories(chatId);
                case "/upcoming" -> handleUpcoming(chatId);
                case "/help" -> handleHelp(chatId);
                default -> handleDefault(chatId, messageText);
            }
        }
    }

    private void handleStart(Long chatId) {
        String text = """
                🎯 Добро пожаловать в Activity Bot!
                
                Я помогу найти интересные активности в вашем городе:
                • Квесты и escape rooms
                • Футбольные матчи
                • Концерты и выставки
                • Мастер-классы
                • И многое другое!
                
                📋 Доступные команды:
                /search - Поиск активностей
                /categories - Категории активностей
                /upcoming - Предстоящие события
                /help - Помощь
                
                Начните с /search чтобы найти что-то интересное!
                """;
        sendMessage(chatId, text);
    }

    private void handleSearch(Long chatId) {
        List<Activity> activities = activityService.getAllActiveActivities();
        
        if (activities.isEmpty()) {
            sendMessage(chatId, "😔 Пока нет доступных активностей. Загляните позже!");
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
            text.append("\n");
        });

        if (activities.size() > 10) {
            text.append("... и ещё ").append(activities.size() - 10).append(" активностей\n");
        }

        sendMessage(chatId, text.toString());
    }

    private void handleCategories(Long chatId) {
        List<Category> categories = activityService.getAllActiveCategories();
        
        if (categories.isEmpty()) {
            sendMessage(chatId, "📂 Категории загружаются...");
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

        sendMessage(chatId, text.toString());
    }

    private void handleUpcoming(Long chatId) {
        List<Activity> activities = activityService.getAllActiveActivities();
        
        // Фильтруем активности с будущим startTime
        List<Activity> upcoming = activities.stream()
                .filter(a -> a.getStartTime() != null && a.getStartTime().isAfter(java.time.LocalDateTime.now()))
                .limit(5)
                .toList();
        
        if (upcoming.isEmpty()) {
            sendMessage(chatId, "📅 Пока нет предстоящих активностей. Следите за обновлениями!");
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

        sendMessage(chatId, text.toString());
    }

    private void handleHelp(Long chatId) {
        String text = """
                ℹ️ Помощь по Activity Bot
                
                Этот бот помогает находить интересные активности в городе.
                
                📋 Команды:
                /start - Начать работу с ботом
                /search - Показать все активности
                /categories - Показать категории
                /upcoming - Предстоящие события
                /help - Эта справка
                
                💡 Советы:
                - Регулярно проверяйте бота для новых активностей
                - Следите за предстоящими событиями в /upcoming
                - Используйте /categories чтобы узнать категории
                
                Есть вопросы? Свяжитесь с разработчиком @xmlreader
                """;
        sendMessage(chatId, text);
    }

    private void handleDefault(Long chatId, String message) {
        sendMessage(chatId, "Я пока не понимаю эту команду. Используйте /help для списка доступных команд.");
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("Markdown");

        try {
            execute(message);
            log.debug("Message sent to {}: {}", chatId, text.substring(0, Math.min(50, text.length())));
        } catch (TelegramApiException e) {
            log.error("Failed to send message to {}: {}", chatId, e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        // Token будет получен из конфигурации Spring
        return System.getenv("TELEGRAM_BOT_TOKEN");
    }
}
