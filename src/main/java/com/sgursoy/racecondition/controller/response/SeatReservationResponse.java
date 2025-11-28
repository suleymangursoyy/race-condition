package com.sgursoy.racecondition.controller.response;

import com.sgursoy.racecondition.repository.entity.Seat;
import com.sgursoy.racecondition.service.ErrorCodes;

public class SeatReservationResponse {

  private ErrorCodes errorCode;

  private Seat seat;

  public ErrorCodes getErrorCode() {
    return errorCode;
  }

  public SeatReservationResponse setErrorCode(ErrorCodes errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  public Seat getSeat() {
    return seat;
  }

  public SeatReservationResponse setSeat(Seat seat) {
    this.seat = seat;
    return this;
  }
}
