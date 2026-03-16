package com.github.xmlreader.activitybot.service.bot.command;

import com.github.xmlreader.activitybot.service.ActivityService;
import com.github.xmlreader.activitybot.service.BookingService;
import com.github.xmlreader.activitybot.service.bot.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotCommandExecutor {
    
    private final MessageSender messageSender;
    private final ActivityService activityService;
    private final BookingService bookingService;
    private final Map<String, BotCommand> commands;
    private final BookCommand bookCommand;
    private final CancelBookingCommand cancelBookingCommand;
    private final CreateActivityCommand createActivityCommand;
    private final CancelCreateCommand cancelCreateCommand;
    private final NotificationsCommand notificationsCommand;
    private final SetReminderCommand setReminderCommand;

    public void execute(Long chatId, String userName, String fullCommand) {
        String[] parts = fullCommand.split(" ", 2);
        String command = parts[0];
        String argument = parts.length > 1 ? parts[1].trim() : null;
        
        // Handle special commands with arguments
        if ("/book".equals(command) && argument != null) {
            try {
                Long activityId = Long.parseLong(argument);
                bookCommand.confirmBooking(chatId, userName, activityId);
                return;
            } catch (NumberFormatException e) {
                bookCommand.execute(chatId);
                return;
            }
        }
        
        if ("/cancel_booking".equals(command) && argument != null) {
            try {
                Long bookingId = Long.parseLong(argument);
                cancelBookingCommand.executeWithId(chatId, bookingId);
                return;
            } catch (NumberFormatException e) {
                cancelBookingCommand.execute(chatId);
                return;
            }
        }
        
        // Handle create activity commands
        if ("/create".equals(command)) {
            createActivityCommand.execute(chatId);
            return;
        }
        
        if ("/cancel_create".equals(command)) {
            cancelCreateCommand.execute(chatId);
            return;
        }
        
        // Handle notification commands
        if ("/notifications".equals(command)) {
            notificationsCommand.execute(chatId);
            return;
        }
        
        if ("/set_reminder".equals(command)) {
            setReminderCommand.executeWithArgs(chatId, argument);
            return;
        }
        
        // Handle standard commands
        BotCommand botCommand = commands.get(command);
        
        if (botCommand == null) {
            messageSender.sendMessage(chatId, "Неизвестная команда. Используйте /help для справки.");
            return;
        }

        try {
            botCommand.execute(chatId);
        } catch (Exception e) {
            log.error("Error executing command {}: {}", command, e.getMessage(), e);
            messageSender.sendMessage(chatId, "Произошла ошибка при выполнении команды. Попробуйте позже.");
        }
    }
}
