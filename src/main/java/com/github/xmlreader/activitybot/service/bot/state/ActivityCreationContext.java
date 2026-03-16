package com.github.xmlreader.activitybot.service.bot.state;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCreationContext {
    
    private Long userTelegramId;
    private ActivityCreationState state;
    
    // Activity data
    private String title;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String location;
    private BigDecimal price;
    private Integer maxParticipants;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    public void reset() {
        this.state = ActivityCreationState.NONE;
        this.title = null;
        this.description = null;
        this.categoryId = null;
        this.categoryName = null;
        this.location = null;
        this.price = null;
        this.maxParticipants = null;
        this.startTime = null;
        this.endTime = null;
    }
}
