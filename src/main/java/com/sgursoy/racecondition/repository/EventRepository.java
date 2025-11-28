package com.sgursoy.racecondition.repository;

import com.sgursoy.racecondition.repository.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

  Event getEventByEventId(Long eventId);

}
