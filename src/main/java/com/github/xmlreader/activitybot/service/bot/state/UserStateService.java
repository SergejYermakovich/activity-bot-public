package com.github.xmlreader.activitybot.service.bot.state;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UserStateService {
    
    private final Map<Long, ActivityCreationContext> userStates = new ConcurrentHashMap<>();
    
    public ActivityCreationContext getState(Long userTelegramId) {
        return userStates.computeIfAbsent(userTelegramId, 
            id -> ActivityCreationContext.builder()
                .userTelegramId(id)
                .state(ActivityCreationState.NONE)
                .build()
        );
    }
    
    public void updateState(Long userTelegramId, ActivityCreationState state) {
        ActivityCreationContext context = getState(userTelegramId);
        context.setState(state);
        log.debug("User {} state updated to {}", userTelegramId, state);
    }
    
    public void updateContext(Long userTelegramId, ActivityCreationContext context) {
        userStates.put(userTelegramId, context);
    }
    
    public void clearState(Long userTelegramId) {
        ActivityCreationContext context = getState(userTelegramId);
        context.reset();
        context.setState(ActivityCreationState.NONE);
        log.debug("User {} state cleared", userTelegramId);
    }
    
    public boolean hasActiveCreation(Long userTelegramId) {
        ActivityCreationContext context = userStates.get(userTelegramId);
        return context != null && context.getState() != ActivityCreationState.NONE;
    }
}
