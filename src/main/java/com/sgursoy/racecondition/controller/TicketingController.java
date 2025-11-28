package com.sgursoy.racecondition.controller;

import com.sgursoy.racecondition.controller.request.SeatReservationRequest;
import com.sgursoy.racecondition.controller.response.SeatReservationResponse;
import com.sgursoy.racecondition.service.TicketingService;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController("ticketing")
public class TicketingController {

  private final TicketingService ticketingService;

  public TicketingController(TicketingService ticketingService) {
    this.ticketingService = ticketingService;
  }

  @GetMapping("pessimistic-locking")
  public ResponseEntity<SeatReservationResponse> ticketingByPessimisticLocking(@Validated @RequestBody SeatReservationRequest request) {
    SeatReservationResponse response = ticketingService.makeReservationForUserWithPessimisticLocking(request);
    if(Objects.nonNull(response.getErrorCode())){
      return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }
    return ResponseEntity.status(OK).body(response);
  }



}
