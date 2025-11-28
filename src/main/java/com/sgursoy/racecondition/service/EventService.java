package com.sgursoy.racecondition.service;

import com.sgursoy.racecondition.repository.EventRepository;
import com.sgursoy.racecondition.repository.entity.Event;
import org.springframework.stereotype.Service;

@Service
public class EventService {

  private final EventRepository eventRepository;

  public EventService(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  public Event getEventById(Long eventId){
    return eventRepository.getEventByEventId(eventId);
  }
}
