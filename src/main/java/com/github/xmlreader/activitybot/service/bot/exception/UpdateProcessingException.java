package com.github.xmlreader.activitybot.service.bot.exception;

public class UpdateProcessingException extends RuntimeException {
    public UpdateProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UpdateProcessingException(String message) {
        super(message);
    }
}
