package com.github.xmlreader.activitybot.service;

import com.github.xmlreader.activitybot.dto.BookingRequest;
import com.github.xmlreader.activitybot.dto.BookingResponse;
import com.github.xmlreader.activitybot.entity.Activity;
import com.github.xmlreader.activitybot.entity.Booking;
import com.github.xmlreader.activitybot.exception.NotFoundException;
import com.github.xmlreader.activitybot.exception.ValidationException;
import com.github.xmlreader.activitybot.repository.ActivityRepository;
import com.github.xmlreader.activitybot.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final ActivityRepository activityRepository;
    
    @Transactional
    public BookingResponse createBooking(BookingRequest request, Long userTelegramId, String userName) {
        Activity activity = activityRepository.findById(request.getActivityId())
                .filter(Activity::getIsActive)
                .orElseThrow(() -> new NotFoundException("Активность", request.getActivityId()));
        
        if (activity.isFull()) {
            throw new ValidationException("К сожалению, все места уже заняты");
        }
        
        int requestedSpots = request.getParticipantsCount() != null 
                ? request.getParticipantsCount() : 1;
        
        if (activity.getAvailableSpots() < requestedSpots) {
            throw new ValidationException(
                "Доступно только " + activity.getAvailableSpots() + " мест"
            );
        }
        
        if (activity.getMinParticipants() != null && requestedSpots < activity.getMinParticipants()) {
            throw new ValidationException(
                "Минимальное количество участников: " + activity.getMinParticipants()
            );
        }
        
        boolean alreadyBooked = bookingRepository.existsByUserTelegramIdAndActivityIdAndStatusIn(
            userTelegramId,
            request.getActivityId(),
            List.of(Booking.BookingStatus.CONFIRMED, Booking.BookingStatus.WAITLIST)
        );
        
        if (alreadyBooked) {
            throw new ValidationException("Вы уже записаны на это мероприятие");
        }
        
        Double totalPrice = activity.getPrice() != null 
                ? activity.getPrice().doubleValue() * requestedSpots 
                : null;
        
        Booking booking = Booking.builder()
                .userId(null)
                .userTelegramId(userTelegramId)
                .userName(userName != null ? userName : "User")
                .activity(activity)
                .participantsCount(requestedSpots)
                .status(Booking.BookingStatus.CONFIRMED)
                .totalPrice(totalPrice)
                .build();
        
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Created booking {} for user {} on activity {}", 
                savedBooking.getId(), userTelegramId, activity.getTitle());
        
        return mapToBookingResponse(savedBooking, activity);
    }
    
    public List<BookingResponse> getUserBookings(Long userTelegramId) {
        return bookingRepository.findByUserTelegramId(userTelegramId).stream()
                .map(booking -> mapToBookingResponse(booking, booking.getActivity()))
                .collect(Collectors.toList());
    }
    
    public List<BookingResponse> getUserActiveBookings(Long userTelegramId) {
        return bookingRepository.findByUserTelegramIdAndStatus(
                userTelegramId, Booking.BookingStatus.CONFIRMED).stream()
                .map(booking -> mapToBookingResponse(booking, booking.getActivity()))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void cancelBooking(Long bookingId, Long userTelegramId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование", bookingId));
        
        if (!booking.getUserTelegramId().equals(userTelegramId)) {
            throw new ValidationException("Вы не можете отменить это бронирование");
        }
        
        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new ValidationException("Бронирование уже отменено или завершено");
        }
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("Cancelled booking {} by user {}", bookingId, userTelegramId);
    }
    
    public List<BookingResponse> getActivityBookings(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Активность", activityId));
        
        return bookingRepository.findByActivityIdAndStatus(
                activityId, Booking.BookingStatus.CONFIRMED).stream()
                .map(booking -> mapToBookingResponse(booking, activity))
                .collect(Collectors.toList());
    }
    
    private BookingResponse mapToBookingResponse(Booking booking, Activity activity) {
        return BookingResponse.builder()
                .id(booking.getId())
                .activityId(activity.getId())
                .activityTitle(activity.getTitle())
                .userId(booking.getUserId())
                .userTelegramId(booking.getUserTelegramId())
                .userName(booking.getUserName())
                .participantsCount(booking.getParticipantsCount())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .createdAt(booking.getCreatedAt())
                .activityStartTime(activity.getStartTime())
                .build();
    }
}
