package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.service.ActivityService;
import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import com.github.xmlreader.activitybot.service.bot.state.ActivityCreationState;
import com.github.xmlreader.activitybot.service.bot.state.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateActivityCommand implements BotCommand {
    
    private final MessageSender messageSender;
    private final UserStateService userStateService;
    private final ActivityService activityService;

    @Override
    public String getCommand() {
        return "/create";
    }

    @Override
    public String getDescription() {
        return "Создать новую активность";
    }

    @Override
    public void execute(Long chatId) {
        // Проверяем, есть ли активный процесс создания
        if (userStateService.hasActiveCreation(chatId)) {
            messageSender.sendMessage(chatId, 
                "⚠️ У вас уже есть незавершённый процесс создания активности.\n\n" +
                "Завершите его или отмените командой /cancel_create"
            );
            return;
        }
        
        // Инициализируем состояние
        userStateService.updateState(chatId, ActivityCreationState.ENTERING_TITLE);
        
        String instructionText = """
            📝 *Создание новой активности*
            
            Давайте создадим активность пошагово. Отвечайте на вопросы по порядку.
            
            🔢 *Шаг 1/8: Название*
            
            Введите название активности (например, "Футбольный матч", "Квест в лесу"):
            
            ---
            ❌ Отмена: /cancel_create
            """;
        
        messageSender.sendMessage(chatId, instructionText);
    }
}
