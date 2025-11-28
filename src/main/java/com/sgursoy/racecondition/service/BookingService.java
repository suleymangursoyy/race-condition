package com.sgursoy.racecondition.service;

import com.sgursoy.racecondition.repository.BookingRepository;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

  private final BookingRepository bookingRepository;

  public BookingService(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }
}
