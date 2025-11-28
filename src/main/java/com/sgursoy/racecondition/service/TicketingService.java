package com.sgursoy.racecondition.service;

import com.sgursoy.racecondition.controller.request.SeatReservationRequest;
import com.sgursoy.racecondition.controller.response.SeatReservationResponse;
import com.sgursoy.racecondition.repository.entity.Event;
import com.sgursoy.racecondition.repository.entity.Seat;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sgursoy.racecondition.repository.entity.SeatStatus.AVAILABLE;
import static com.sgursoy.racecondition.repository.entity.SeatStatus.RESERVED;
import static com.sgursoy.racecondition.service.ErrorCodes.EVENT_NOT_FOUND;

@Service
public class TicketingService {

  private final SeatService seatService;

  private final EventService eventService;

  private final ReservationService reservationService;

  private LocalDateTime reservationTime;

  public TicketingService(SeatService seatService, EventService eventService, ReservationService reservationService) {
    this.seatService = seatService;
    this.eventService = eventService;
    this.reservationService = reservationService;
  }

  @Transactional
  public SeatReservationResponse makeReservationForUserWithPessimisticLocking(SeatReservationRequest request) {
    SeatReservationResponse response = new SeatReservationResponse();
    Event event = eventService.getEventById(request.getEventId());
    if(Objects.isNull(event)) {
      return response.setErrorCode(EVENT_NOT_FOUND);
    }
    Seat seat = seatService.getSeatByEventIdAndSeatIdWithPessimisticLocking(request.getEventId(),
                                                                            request.getSeatNumber()).get();
    if (Objects.isNull(seat)) {
      return response.setErrorCode(ErrorCodes.SEAT_NOT_FOUND);
    }
    if(!AVAILABLE.equals(seat.getStatus())){
      return response.setErrorCode(ErrorCodes.SEAT_NOT_AVAILABLE);
    }

    reservationTime = LocalDateTime.now().plusMinutes(10);
    seat.setStatus(RESERVED);
    seat.setReservedBy(request.getUserId());
    seat.setReservedUntil(reservationTime);
    seatService.save(seat);

    reservationService.createReservation(request.getUserId(), seat, event, reservationTime);
    return response;
  }

}
