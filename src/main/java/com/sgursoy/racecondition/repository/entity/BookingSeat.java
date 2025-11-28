package com.sgursoy.racecondition.repository.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "booking_seats", uniqueConstraints = {
    @UniqueConstraint(name = "uk_booking_seat", columnNames = {"booking_id", "seat_id"})
})
@Builder
@Getter
@Setter
public class BookingSeat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long bookingSeatId; // Corresponds to booking_seat_id (Primary Key)

  // Foreign Key to the Booking table
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "booking_id", nullable = false)
  private Booking booking;

  // Foreign Key to the Seat table
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seat_id", nullable = false)
  private Seat seat; // Assumes Seat entity exists

  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  public BookingSeat() {

  }

  // --- Constructors, Getters, and Setters (omitted for brevity) ---
}
