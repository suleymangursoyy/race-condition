package com.sgursoy.racecondition.service;

import com.sgursoy.racecondition.repository.ReservationRepository;
import com.sgursoy.racecondition.repository.entity.Event;
import com.sgursoy.racecondition.repository.entity.Reservation;
import com.sgursoy.racecondition.repository.entity.Seat;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

  private final ReservationRepository reservationRepository;

  public ReservationService(ReservationRepository reservationRepository) {
    this.reservationRepository = reservationRepository;
  }

  public Reservation createReservation(String userId, Seat seat, Event event, LocalDateTime reservationTime) {
    return reservationRepository.save(
        new Reservation().builder().userId(userId).seat(seat).event(event).expiresAt(reservationTime).build());
  }
}
