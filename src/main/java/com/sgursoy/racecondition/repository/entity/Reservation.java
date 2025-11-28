package com.sgursoy.racecondition.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reservations")
@Builder
@Getter
@Setter
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reservationId; // Corresponds to reservation_id (Primary Key)

  // Foreign Key to the Seat table
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seat_id", nullable = false)
  private Seat seat; // Assumes Seat entity exists

  // Foreign Key to the Event table
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false)
  private Event event; // Assumes Event entity exists

  @Column(name = "user_id", nullable = false, length = 50)
  private String userId;

  @Column(name = "session_id", length = 100)
  private String sessionId;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "ENUM('ACTIVE', 'CONFIRMED', 'EXPIRED', 'CANCELLED')")
  private ReservationStatus status = ReservationStatus.ACTIVE; // Maps to ENUM('ACTIVE', ...)

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  public Reservation() {

  }
}
