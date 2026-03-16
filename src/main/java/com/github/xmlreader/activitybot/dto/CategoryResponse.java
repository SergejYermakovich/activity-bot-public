package com.github.xmlreader.activitybot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    
    private Long id;
    private String name;
    private String description;
    private String emoji;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Integer activityCount;
}
