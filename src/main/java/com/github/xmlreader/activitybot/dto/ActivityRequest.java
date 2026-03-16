package com.github.xmlreader.activitybot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRequest {
    
    @NotBlank(message = "Название обязательно")
    private String title;
    
    private String description;
    
    @NotNull(message = "Категория обязательна")
    private Long categoryId;
    
    @NotBlank(message = "Локация обязательна")
    private String location;
    
    private BigDecimal price;
    
    private Integer minParticipants;
    
    private Integer maxParticipants;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
}
