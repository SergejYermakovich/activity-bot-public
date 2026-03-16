package com.github.xmlreader.activitybot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    
    @NotNull(message = "ID активности обязателен")
    private Long activityId;
    
    @Min(value = 1, message = "Минимум 1 участник")
    @Builder.Default
    private Integer participantsCount = 1;
    
    private String userName;
    
    private String userPhone;
}
