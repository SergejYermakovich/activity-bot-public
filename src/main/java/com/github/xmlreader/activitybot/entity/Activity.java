package com.github.xmlreader.activitybot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(nullable = false)
    private String location;
    
    private BigDecimal price;
    
    @Column(name = "min_participants")
    private Integer minParticipants;
    
    @Column(name = "max_participants")
    private Integer maxParticipants;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @OneToMany(mappedBy = "activity", fetch = FetchType.LAZY)
    private Set<Booking> bookings = new HashSet<>();
    
    public int getBookedParticipantsCount() {
        if (bookings == null) return 0;
        return bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .mapToInt(Booking::getParticipantsCount)
                .sum();
    }
    
    public int getAvailableSpots() {
        if (maxParticipants == null) return Integer.MAX_VALUE;
        return maxParticipants - getBookedParticipantsCount();
    }
    
    public boolean isFull() {
        return getAvailableSpots() <= 0;
    }
}
