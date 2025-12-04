package com.sgursoy.racecondition.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservationRequest {

  @NotNull
  @Size(min = 2)
  private String userId;

  @NotNull
  @Min(0)
  private Long eventId;

  @NotNull
  @Size(min = 1)
  private String seatNumber;
}
