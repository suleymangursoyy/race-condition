package com.sgursoy.racecondition.service;

public enum ErrorCodes {

  SEAT_NOT_AVAILABLE(1001, "SEAT NOT AVAILABLE"),

  SEAT_NOT_FOUND(1002, "SEAT NOT FOUND"),

  EVENT_NOT_FOUND(1003, "EVENT NOT FOUND");

  private String message;

  private Integer code;

  ErrorCodes(Integer code, String message) {
    this.code = code;
    this.message = message;
  }


}
