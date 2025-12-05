package com.sgursoy.racecondition.service;

import com.sgursoy.racecondition.repository.SeatRepository;
import com.sgursoy.racecondition.repository.entity.Seat;
import com.sgursoy.racecondition.repository.entity.enums.SeatStatus;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SeatService {

  private final SeatRepository seatRepository;

  public SeatService(SeatRepository seatRepository) {
    this.seatRepository = seatRepository;
  }

  public Optional<Seat> getSeatByEventIdAndSeatIdWithPessimisticLocking(Long eventId, String seatNumber) {
    return seatRepository.findByEventIdAndSeatNumberWithPessimisticLock(eventId, seatNumber);
  }

  public Optional<Seat> getSeatByEventIdAndSeatIdWithOptimisticLocking(Long eventId, String seatNumber) {
    return seatRepository.findByEventIdAndSeatNumber(eventId, seatNumber);
  }

  public void save(Seat seat) {
    seatRepository.save(seat);
  }

  public Integer updateSeatStatusWithVersion(Long seatId, LocalDateTime reservationTime, String reservedBy,
      Long version,
      SeatStatus status) {
    return seatRepository.updateSeatStatusWithVersion(seatId, reservationTime, reservedBy, version, status);
  }
}
