package com.github.xmlreader.activitybot.exception;

public class NotFoundException extends RuntimeException {
    
    public NotFoundException(String entityType, Long id) {
        super(entityType + " не найден с ID: " + id);
    }
    
    public NotFoundException(String message) {
        super(message);
    }
}
