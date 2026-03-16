package com.github.xmlreader.activitybot.service.bot.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class MessageSender {
    
    private final OkHttpTelegramClient telegramClient;

    public MessageSender(OkHttpTelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage request = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("Markdown")
                .build();

        try {
            Message response = telegramClient.execute(request);
            log.debug("Message sent to {}: {}", chatId, text.substring(0, Math.min(50, text.length())));
        } catch (TelegramApiException e) {
            log.error("Failed to send message to {}: {}", chatId, e.getMessage());
        }
    }

    public void sendMessage(Long chatId, String text, Boolean disablePreview) {
        SendMessage request = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("Markdown")
                .linkPreviewOptions(options -> options.setIsDisabled(disablePreview))
                .build();

        try {
            telegramClient.execute(request);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to {}: {}", chatId, e.getMessage());
        }
    }
}
