package com.sgursoy.racecondition.controller.response;

import com.sgursoy.racecondition.service.ErrorCodes;

public class SeatReservationResponse {

  private ErrorCodes errorCode;

  private Long seatId;

  private Long reservationId;

  public ErrorCodes getErrorCode() {
    return errorCode;
  }

  public SeatReservationResponse setErrorCode(ErrorCodes errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  public Long getSeatId() {
    return seatId;
  }

  public SeatReservationResponse setSeatId(Long seatId) {
    this.seatId = seatId;
    return this;
  }

  public Long getReservationId() {
    return reservationId;
  }

  public SeatReservationResponse setReservationId(Long reservationId) {
    this.reservationId = reservationId;
    return this;
  }
}
