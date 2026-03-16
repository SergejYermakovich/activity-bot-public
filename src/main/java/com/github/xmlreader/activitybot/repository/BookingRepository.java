package com.github.xmlreader.activitybot.repository;

import com.github.xmlreader.activitybot.entity.Booking;
import com.github.xmlreader.activitybot.entity.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUserTelegramId(Long userTelegramId);
    
    List<Booking> findByUserTelegramIdAndStatus(Long userTelegramId, BookingStatus status);
    
    List<Booking> findByActivityIdAndStatus(Long activityId, BookingStatus status);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.activity.id = :activityId AND b.status IN :statuses")
    Long countByActivityIdAndStatusIn(@Param("activityId") Long activityId, 
                                       @Param("statuses") List<BookingStatus> statuses);
    
    boolean existsByUserTelegramIdAndActivityIdAndStatusIn(
        Long userTelegramId, 
        Long activityId, 
        List<BookingStatus> statuses
    );
}
