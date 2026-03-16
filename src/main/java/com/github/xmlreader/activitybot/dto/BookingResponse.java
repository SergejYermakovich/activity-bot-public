package com.github.xmlreader.activitybot.dto;

import com.github.xmlreader.activitybot.entity.Booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    
    private Long id;
    private Long activityId;
    private String activityTitle;
    private Long userId;
    private Long userTelegramId;
    private String userName;
    private Integer participantsCount;
    private BookingStatus status;
    private Double totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime activityStartTime;
}
