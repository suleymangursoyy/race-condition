package com.sgursoy.racecondition.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SeatReservationRequest {

  @NotNull
  @Min(2)
  private String userId;

  @NotNull
  @Min(0)
  private Long eventId;

  @NotNull
  @Min(2)
  private String seatNumber;
}
