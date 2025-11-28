package com.sgursoy.racecondition.service;

import com.sgursoy.racecondition.repository.SeatRepository;
import com.sgursoy.racecondition.repository.entity.Seat;
import java.util.Optional;
import org.springframework.stereotype.Service;


@Service
public class SeatService {

  private final SeatRepository seatRepository;

  public SeatService(SeatRepository seatRepository) {
    this.seatRepository = seatRepository;
  }

  public Optional<Seat> getSeatByEventIdAndSeatIdWithPessimisticLocking(Long eventId, String seatNumber) {
    return seatRepository.findByEventIdAndSeatNumberWithLock(eventId, seatNumber);
  }

  public void save(Seat seat) {
    seatRepository.save(seat);
  }
}
