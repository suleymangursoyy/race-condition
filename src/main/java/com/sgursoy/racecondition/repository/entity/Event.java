package com.sgursoy.racecondition.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_sale_start_time", columnList = "sale_start_time"),
    @Index(name = "idx_status", columnList = "status")
})
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long eventId; // Corresponds to event_id (Primary Key)

  @Column(name = "event_name", nullable = false, length = 255)
  private String eventName;

  @Column(name = "event_date", nullable = false)
  private LocalDateTime eventDate;

  @Column(name = "venue_name", length = 255)
  private String venueName;

  @Column(name = "total_seats", nullable = false)
  private Integer totalSeats;

  @Column(name = "available_seats", nullable = false)
  private Integer availableSeats;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "ENUM('UPCOMING', 'ON_SALE', 'SOLD_OUT', 'CANCELLED')")
  @Builder.Default
  private EventStatus status = EventStatus.UPCOMING; // Maps to ENUM

  @Column(name = "sale_start_time")
  private LocalDateTime saleStartTime;

  @Column(name = "created_at", nullable = false)
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();

  // Relationship: One Event has many Seats
  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Seat> seats;
}
