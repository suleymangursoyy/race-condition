package com.sgursoy.racecondition.repository;

import com.sgursoy.racecondition.repository.entity.Seat;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM Seat s where s.event.eventId = :eventId AND s.seatNumber = :seatNumber")
  Optional<Seat> findByEventIdAndSeatNumberWithLock(@Param("eventId") Long eventId, @Param("seatNumber")String seatNumber);

}
