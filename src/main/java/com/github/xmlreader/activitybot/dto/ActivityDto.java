package com.github.xmlreader.activitybot.dto;

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
public class ActivityDto {
    
    private Long id;
    private String title;
    private String description;
    private String categoryName;
    private String categoryEmoji;
    private String location;
    private BigDecimal price;
    private Integer minParticipants;
    private Integer maxParticipants;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    public String toTelegramMessage() {
        StringBuilder sb = new StringBuilder();
        
        if (categoryEmoji != null) {
            sb.append(categoryEmoji).append(" ");
        }
        
        sb.append("**").append(title).append("**\n");
        
        if (description != null && !description.isEmpty()) {
            sb.append(description).append("\n");
        }
        
        sb.append("\n📍 **Место:** ").append(location);
        
        if (price != null) {
            sb.append("\n💰 **Цена:** ").append(price).append(" руб.");
        }
        
        if (startTime != null) {
            sb.append("\n🕐 **Начало:** ").append(startTime.toLocalDate())
              .append(" в ").append(startTime.toLocalTime());
        }
        
        if (minParticipants != null || maxParticipants != null) {
            sb.append("\n👥 **Участники:** ");
            if (minParticipants != null) sb.append("от ").append(minParticipants);
            if (maxParticipants != null) sb.append(" до ").append(maxParticipants);
        }
        
        return sb.toString();
    }
}
