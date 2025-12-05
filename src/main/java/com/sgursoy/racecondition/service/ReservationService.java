package com.sgursoy.racecondition.service;

import com.sgursoy.racecondition.controller.request.SeatReservationRequest;
import com.sgursoy.racecondition.controller.response.SeatReservationResponse;
import com.sgursoy.racecondition.repository.ReservationRepository;
import com.sgursoy.racecondition.repository.entity.Event;
import com.sgursoy.racecondition.repository.entity.Reservation;
import com.sgursoy.racecondition.repository.entity.Seat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sgursoy.racecondition.repository.entity.enums.SeatStatus.AVAILABLE;
import static com.sgursoy.racecondition.repository.entity.enums.SeatStatus.RESERVED;
import static com.sgursoy.racecondition.service.ErrorCodes.EVENT_NOT_FOUND;

@Service
public class ReservationService {

  private final static Integer ZERO = 0;

  private final SeatService seatService;

  private final EventService eventService;

  private final ReservationRepository reservationRepository;

  public ReservationService(ReservationRepository reservationRepository, EventService eventService, SeatService seatService) {
    this.seatService = seatService;
    this.eventService = eventService;
    this.reservationRepository = reservationRepository;
  }

  public Reservation createReservation(String userId, Seat seat, Event event, LocalDateTime reservationTime) {
    return reservationRepository.save(
        Reservation.builder().userId(userId).seat(seat).event(event).expiresAt(reservationTime).build());
  }

  @Transactional
  public SeatReservationResponse makeReservationForUserWithPessimisticLocking(SeatReservationRequest request) {
    SeatReservationResponse response = new SeatReservationResponse();
    Event event = eventService.getEventById(request.getEventId());
    if (Objects.isNull(event)) {
      return response.setErrorCode(EVENT_NOT_FOUND);
    }
    Optional<Seat> seatOptional = seatService.getSeatByEventIdAndSeatIdWithPessimisticLocking(request.getEventId(),
        request.getSeatNumber());
    if (seatOptional.isEmpty()) {
      return response.setErrorCode(ErrorCodes.SEAT_NOT_FOUND);
    }
    Seat seat = seatOptional.get();
    if (!AVAILABLE.equals(seat.getStatus())) {
      return response.setErrorCode(ErrorCodes.SEAT_NOT_AVAILABLE);
    }

    LocalDateTime reservationTime = LocalDateTime.now().plusMinutes(10);
    seat.setStatus(RESERVED);
    seat.setReservedBy(request.getUserId());
    seat.setReservedUntil(reservationTime);
    seatService.save(seat);

    Long reservationId = createReservation(request.getUserId(), seat, event, reservationTime)
        .getReservationId();
    response.setReservationId(reservationId);
    response.setSeatId(seat.getSeatId());

    return response;
  }

  @Transactional
  public SeatReservationResponse makeReservationForUserWithOptimisticLocking(SeatReservationRequest request) {
    SeatReservationResponse response = new SeatReservationResponse();
    Event event = eventService.getEventById(request.getEventId());
    if (Objects.isNull(event)) {
      return response.setErrorCode(EVENT_NOT_FOUND);
    }
    Optional<Seat> seatOptional = seatService.getSeatByEventIdAndSeatIdWithOptimisticLocking(request.getEventId(),
        request.getSeatNumber());
    if (seatOptional.isEmpty()) {
      return response.setErrorCode(ErrorCodes.SEAT_NOT_FOUND);
    }
    Seat seat = seatOptional.get();
    if (!AVAILABLE.equals(seat.getStatus())) {
      return response.setErrorCode(ErrorCodes.SEAT_NOT_AVAILABLE);
    }

    LocalDateTime reservationTime = LocalDateTime.now().plusMinutes(10);
    Integer updatedSeat = seatService.updateSeatStatusWithVersion(seat.getSeatId(), reservationTime, request.getUserId(),
        seat.getVersion(), RESERVED);
    if (ZERO.equals(updatedSeat)) {
      return response.setErrorCode(ErrorCodes.CONCURRENT_MODIFICATION);
    }

    Long reservationId = createReservation(request.getUserId(), seat, event, reservationTime)
        .getReservationId();
    response.setReservationId(reservationId);
    response.setSeatId(seat.getSeatId());
    return response;
  }
}
