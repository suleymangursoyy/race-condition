package com.sgursoy.racecondition.repository.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bookings")
@Builder
@Getter
@Setter
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long bookingId; // Corresponds to booking_id (Primary Key)

  // Foreign Key to the Event table
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false)
  private Event event; // Assumes Event entity exists

  @Column(name = "user_id", nullable = false, length = 50)
  private String userId;

  @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalAmount;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'FAILED')")
  private BookingStatus status = BookingStatus.PENDING; // Maps to ENUM('PENDING', ...)

  @Column(name = "payment_id", length = 100)
  private String paymentId;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", columnDefinition = "ENUM('PENDING', 'SUCCESS', 'FAILED')")
  private PaymentStatus paymentStatus = PaymentStatus.PENDING; // Maps to ENUM('PENDING', ...)

  @Column(name = "booking_reference", nullable = false, unique = true, length = 50)
  private String bookingReference;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "confirmed_at")
  private LocalDateTime confirmedAt;

  // Relationship: One Booking has many BookingSeats
  @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BookingSeat> bookingSeats;

  public Booking() {

  }

  // --- Enum Definitions for Status Fields ---
  public enum BookingStatus {
    PENDING, CONFIRMED, CANCELLED, FAILED
  }

  public enum PaymentStatus {
    PENDING, SUCCESS, FAILED
  }

  // --- Constructors, Getters, and Setters (omitted for brevity) ---
}
