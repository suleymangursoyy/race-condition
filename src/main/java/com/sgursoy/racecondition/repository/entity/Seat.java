package com.sgursoy.racecondition.repository.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(name = "uk_event_seat", columnNames = { "event_id", "seat_number" })
}, indexes = {
    @Index(name = "idx_event_status", columnList = "event_id, status"),
    @Index(name = "idx_reserved_until", columnList = "reserved_until")
})
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long seatId; // Corresponds to seat_id (Primary Key)

  // Foreign Key to the Event table
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  @Column(name = "seat_number", nullable = false, length = 20)
  private String seatNumber;

  @Column(name = "section", length = 50)
  private String section;

  @Column(name = "row_number", length = 10)
  private String rowNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "seat_type", columnDefinition = "ENUM('REGULAR', 'VIP', 'PREMIUM')")
  @Builder.Default
  private SeatType seatType = SeatType.REGULAR; // Maps to ENUM

  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "ENUM('AVAILABLE', 'RESERVED', 'BOOKED', 'BLOCKED')")
  @Builder.Default
  private SeatStatus status = SeatStatus.AVAILABLE; // Maps to ENUM

  @Version // JPA's annotation for optimistic locking
  @Column(name = "version")
  @Builder.Default
  private Long version = 0L;

  @Column(name = "reserved_by", length = 50)
  private String reservedBy;

  @Column(name = "reserved_until")
  private LocalDateTime reservedUntil;

  @Column(name = "booking_id")
  private Long bookingId; // Foreign key placeholder, often better handled through a relationship

  @Column(name = "created_at", nullable = false)
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
}
