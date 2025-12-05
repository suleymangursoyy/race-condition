package com.sgursoy.racecondition.controller;

import com.sgursoy.racecondition.controller.request.SeatReservationRequest;
import com.sgursoy.racecondition.controller.response.SeatReservationResponse;
import com.sgursoy.racecondition.service.ReservationService;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController("reservation/")
public class ReservationController {

  private final ReservationService reservationService;

  public ReservationController(ReservationService reservationService) {
    this.reservationService = reservationService;
  }

  @GetMapping("pessimistic-locking")
  public ResponseEntity<SeatReservationResponse> reservationWithPessimisticLocking(@Validated @RequestBody SeatReservationRequest request) {
    SeatReservationResponse response = reservationService.makeReservationForUserWithPessimisticLocking(request);
    if(Objects.nonNull(response.getErrorCode())){
      return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }
    return ResponseEntity.status(OK).body(response);
  }

  @GetMapping("optimistic-locking")
  public ResponseEntity<SeatReservationResponse> reservationWithOptimisticLocking(@Validated @RequestBody SeatReservationRequest request) {
    SeatReservationResponse response = reservationService.makeReservationForUserWithOptimisticLocking(request);
    if(Objects.nonNull(response.getErrorCode())){
      return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }
    return ResponseEntity.status(OK).body(response);
  }


}
