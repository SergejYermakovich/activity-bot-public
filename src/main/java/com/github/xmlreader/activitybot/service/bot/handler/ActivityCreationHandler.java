package com.github.xmlreader.activitybot.service.bot.handler;

import com.github.xmlreader.activitybot.entity.Activity;
import com.github.xmlreader.activitybot.entity.Category;
import com.github.xmlreader.activitybot.service.ActivityService;
import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import com.github.xmlreader.activitybot.service.bot.state.ActivityCreationContext;
import com.github.xmlreader.activitybot.service.bot.state.ActivityCreationState;
import com.github.xmlreader.activitybot.service.bot.state.UserStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityCreationHandler {
    
    private final UserStateService userStateService;
    private final ActivityService activityService;
    private final MessageSender messageSender;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public void handleTextMessage(Long chatId, String userName, String text) {
        ActivityCreationContext context = userStateService.getState(chatId);
        ActivityCreationState state = context.getState();
        
        if (state == ActivityCreationState.NONE) {
            return;
        }
        
        log.info("User {} in state {} entered: {}", chatId, state, text);
        
        try {
            switch (state) {
                case ENTERING_TITLE -> handleTitle(chatId, context, text);
                case ENTERING_DESCRIPTION -> handleDescription(chatId, context, text);
                case SELECTING_CATEGORY -> handleCategorySelection(chatId, context, text);
                case ENTERING_LOCATION -> handleLocation(chatId, context, text);
                case ENTERING_PRICE -> handlePrice(chatId, context, text);
                case ENTERING_MAX_PARTICIPANTS -> handleMaxParticipants(chatId, context, text);
                case ENTERING_START_TIME -> handleStartTime(chatId, context, text);
                case ENTERING_END_TIME -> handleEndTime(chatId, context, text);
                case CONFIRMING -> handleConfirmation(chatId, context, text, userName);
                default -> messageSender.sendMessage(chatId, "Произошла ошибка. Начните заново: /create");
            }
        } catch (Exception e) {
            log.error("Error in activity creation for user {}: {}", chatId, e.getMessage(), e);
            messageSender.sendMessage(chatId, 
                "❌ Произошла ошибка: " + e.getMessage() + "\n\n" +
                "Попробуйте заново: /create\n" +
                "Или отмените: /cancel_create"
            );
            userStateService.clearState(chatId);
        }
    }
    
    private void handleTitle(Long chatId, ActivityCreationContext context, String text) {
        if (text.isBlank() || text.length() < 3) {
            messageSender.sendMessage(chatId, 
                "❌ Название должно быть не менее 3 символов.\n\n" +
                "Введите название активности:"
            );
            return;
        }
        
        context.setTitle(text.trim());
        userStateService.updateContext(chatId, context);
        
        List<Category> categories = activityService.getAllActiveCategories();
        
        StringBuilder categoryText = new StringBuilder();
        categoryText.append("✅ *Название сохранено: ").append(context.getTitle()).append("*\n\n");
        categoryText.append("🔢 *Шаг 2/8: Категория*\n\n");
        categoryText.append("Выберите категорию (отправьте номер):\n\n");
        
        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            String emoji = cat.getEmoji() != null ? cat.getEmoji() : "📌";
            categoryText.append(i + 1).append(". ").append(emoji).append(" ").append(cat.getName()).append("\n");
        }
        
        categoryText.append("\n---\n❌ Отмена: /cancel_create");
        
        messageSender.sendMessage(chatId, categoryText.toString());
        userStateService.updateState(chatId, ActivityCreationState.SELECTING_CATEGORY);
    }
    
    private void handleCategorySelection(Long chatId, ActivityCreationContext context, String text) {
        try {
            int categoryIndex = Integer.parseInt(text.trim()) - 1;
            List<Category> categories = activityService.getAllActiveCategories();
            
            if (categoryIndex < 0 || categoryIndex >= categories.size()) {
                throw new IllegalArgumentException("Неверный номер категории");
            }
            
            Category selectedCategory = categories.get(categoryIndex);
            context.setCategoryId(selectedCategory.getId());
            context.setCategoryName(selectedCategory.getName());
            userStateService.updateContext(chatId, context);
            
            messageSender.sendMessage(chatId, 
                "✅ *Категория выбрана: " + selectedCategory.getName() + "*\n\n" +
                "🔢 *Шаг 4/8: Локация*\n\n" +
                "Введите место проведения (адрес, название места):\n\n" +
                "---\n❌ Отмена: /cancel_create"
            );
            userStateService.updateState(chatId, ActivityCreationState.ENTERING_LOCATION);
            
        } catch (NumberFormatException e) {
            messageSender.sendMessage(chatId, 
                "❌ Пожалуйста, отправьте номер категории (цифру).\n\n" +
                "Выберите категорию:"
            );
        } catch (IllegalArgumentException e) {
            messageSender.sendMessage(chatId, 
                "❌ " + e.getMessage() + "\n\n" +
                "Выберите категорию (отправьте номер):"
            );
        }
    }
    
    private void handleLocation(Long chatId, ActivityCreationContext context, String text) {
        if (text.isBlank() || text.length() < 3) {
            messageSender.sendMessage(chatId, 
                "❌ Локация должна быть не менее 3 символов.\n\n" +
                "Введите место проведения:"
            );
            return;
        }
        
        context.setLocation(text.trim());
        userStateService.updateContext(chatId, context);
        
        messageSender.sendMessage(chatId, 
            "✅ *Локация сохранена: " + context.getLocation() + "*\n\n" +
            "🔢 *Шаг 5/8: Цена*\n\n" +
            "Введите стоимость участия в BYN (или 0 если бесплатно):\n\n" +
            "---\n❌ Отмена: /cancel_create"
        );
        userStateService.updateState(chatId, ActivityCreationState.ENTERING_PRICE);
    }
    
    private void handlePrice(Long chatId, ActivityCreationContext context, String text) {
        try {
            BigDecimal price = new BigDecimal(text.trim());
            
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                messageSender.sendMessage(chatId, 
                    "❌ Цена не может быть отрицательной.\n\n" +
                    "Введите стоимость (или 0 если бесплатно):"
                );
                return;
            }
            
            context.setPrice(price);
            userStateService.updateContext(chatId, context);
            
            messageSender.sendMessage(chatId, 
                "✅ *Цена сохранена: " + 
                (price.compareTo(BigDecimal.ZERO) == 0 ? "Бесплатно" : price + " BYN") + 
                "*\n\n" +
                "🔢 *Шаг 6/8: Количество мест*\n\n" +
                "Введите максимальное количество участников:\n\n" +
                "---\n❌ Отмена: /cancel_create"
            );
            userStateService.updateState(chatId, ActivityCreationState.ENTERING_MAX_PARTICIPANTS);
            
        } catch (NumberFormatException e) {
            messageSender.sendMessage(chatId, 
                "❌ Пожалуйста, введите число (например, 50 или 0).\n\n" +
                "Введите стоимость:"
            );
        }
    }
    
    private void handleMaxParticipants(Long chatId, ActivityCreationContext context, String text) {
        try {
            int maxParticipants = Integer.parseInt(text.trim());
            
            if (maxParticipants < 1) {
                messageSender.sendMessage(chatId, 
                    "❌ Минимум 1 участник.\n\n" +
                    "Введите максимальное количество участников:"
                );
                return;
            }
            
            context.setMaxParticipants(maxParticipants);
            userStateService.updateContext(chatId, context);
            
            messageSender.sendMessage(chatId, 
                "✅ *Количество мест сохранено: " + maxParticipants + "*\n\n" +
                "🔢 *Шаг 7/8: Дата и время начала*\n\n" +
                "Введите дату и время в формате: ДД.ММ.ГГГГ ЧЧ:ММ\n\n" +
                "Пример: 20.03.2026 19:00\n\n" +
                "---\n❌ Отмена: /cancel_create"
            );
            userStateService.updateState(chatId, ActivityCreationState.ENTERING_START_TIME);
            
        } catch (NumberFormatException e) {
            messageSender.sendMessage(chatId, 
                "❌ Пожалуйста, введите число.\n\n" +
                "Введите максимальное количество участников:"
            );
        }
    }
    
    private void handleStartTime(Long chatId, ActivityCreationContext context, String text) {
        try {
            LocalDateTime startTime = LocalDateTime.parse(text.trim(), DATE_TIME_FORMATTER);
            
            if (startTime.isBefore(LocalDateTime.now())) {
                messageSender.sendMessage(chatId, 
                    "❌ Время начала не может быть в прошлом.\n\n" +
                    "Введите дату и время в формате: ДД.ММ.ГГГГ ЧЧ:ММ\n\n" +
                    "Пример: 20.03.2026 19:00"
                );
                return;
            }
            
            context.setStartTime(startTime);
            userStateService.updateContext(chatId, context);
            
            messageSender.sendMessage(chatId, 
                "✅ *Время начала сохранено: " + startTime.format(DATE_TIME_FORMATTER) + "*\n\n" +
                "🔢 *Шаг 8/8: Дата и время окончания*\n\n" +
                "Введите дату и время окончания в формате: ДД.ММ.ГГГГ ЧЧ:ММ\n\n" +
                "Пример: 20.03.2026 21:00\n\n" +
                "---\n❌ Отмена: /cancel_create"
            );
            userStateService.updateState(chatId, ActivityCreationState.ENTERING_END_TIME);
            
        } catch (DateTimeParseException e) {
            messageSender.sendMessage(chatId, 
                "❌ Неверный формат даты.\n\n" +
                "Используйте формат: ДД.ММ.ГГГГ ЧЧ:ММ\n\n" +
                "Пример: 20.03.2026 19:00\n\n" +
                "Введите дату и время начала:"
            );
        }
    }
    
    private void handleEndTime(Long chatId, ActivityCreationContext context, String text) {
        try {
            LocalDateTime endTime = LocalDateTime.parse(text.trim(), DATE_TIME_FORMATTER);
            
            if (endTime.isBefore(context.getStartTime())) {
                messageSender.sendMessage(chatId, 
                    "❌ Время окончания не может быть раньше времени начала.\n\n" +
                    "Введите дату и время окончания:"
                );
                return;
            }
            
            context.setEndTime(endTime);
            userStateService.updateContext(chatId, context);
            userStateService.updateState(chatId, ActivityCreationState.CONFIRMING);
            
            // Показываем подтверждение
            showConfirmation(chatId, context);
            
        } catch (DateTimeParseException e) {
            messageSender.sendMessage(chatId, 
                "❌ Неверный формат даты.\n\n" +
                "Используйте формат: ДД.ММ.ГГГГ ЧЧ:ММ\n\n" +
                "Пример: 20.03.2026 21:00\n\n" +
                "Введите дату и время окончания:"
            );
        }
    }
    
    private void showConfirmation(Long chatId, ActivityCreationContext context) {
        String confirmationText = String.format("""
            ✅ *Все данные введены! Проверьте:*\n\n
            📍 *%s*\n
            📂 Категория: %s\n
            🗺️ Локация: %s\n
            💰 Цена: %s\n
            👥 Мест: %d\n
            🕐 Начало: %s\n
            🕐 Окончание: %s\n\n
            ---
            Всё верно? Отправьте *ДА* для создания
            Или *НЕТ* чтобы отменить
            """,
            context.getTitle(),
            context.getCategoryName(),
            context.getLocation(),
            context.getPrice().compareTo(BigDecimal.ZERO) == 0 ? "Бесплатно" : context.getPrice() + " BYN",
            context.getMaxParticipants(),
            context.getStartTime().format(DATE_TIME_FORMATTER),
            context.getEndTime().format(DATE_TIME_FORMATTER)
        );
        
        messageSender.sendMessage(chatId, confirmationText);
    }
    
    private void handleConfirmation(Long chatId, ActivityCreationContext context, String text, String userName) {
        if (!text.equalsIgnoreCase("да") && !text.equalsIgnoreCase("yes")) {
            userStateService.clearState(chatId);
            messageSender.sendMessage(chatId, 
                "❌ *Создание отменено!*\n\n" +
                "Начать заново: /create"
            );
            return;
        }
        
        // Создаём активность
        Activity activity = Activity.builder()
            .title(context.getTitle())
            .description(context.getDescription())
            .category(com.github.xmlreader.activitybot.entity.Category.builder()
                .id(context.getCategoryId())
                .name(context.getCategoryName())
                .build())
            .location(context.getLocation())
            .price(context.getPrice())
            .maxParticipants(context.getMaxParticipants())
            .startTime(context.getStartTime())
            .endTime(context.getEndTime())
            .isActive(true)
            .build();
        
        Activity created = activityService.createActivity(activity);
        
        userStateService.clearState(chatId);
        
        String successText = String.format("""
            🎉 *Активность создана!*\n\n
            📍 *%s*\n
            🎫 ID: #%d\n
            📂 Категория: %s\n
            🗺️ Локация: %s\n
            💰 Цена: %s\n
            👥 Мест: %d\n
            🕐 %s - %s\n\n
            Активность появится в поиске после модерации.
            
            ---
            Создать ещё одну: /create
            """,
            created.getTitle(),
            created.getId(),
            context.getCategoryName(),
            context.getLocation(),
            context.getPrice().compareTo(BigDecimal.ZERO) == 0 ? "Бесплатно" : context.getPrice() + " BYN",
            context.getMaxParticipants(),
            created.getStartTime().format(DATE_TIME_FORMATTER),
            created.getEndTime().format(DATE_TIME_FORMATTER)
        );
        
        messageSender.sendMessage(chatId, successText);
        
        log.info("Activity created by user {}: {}", chatId, created.getTitle());
    }
}
