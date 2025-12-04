package com.sgursoy.racecondition.repository;

import com.sgursoy.racecondition.repository.entity.Seat;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM Seat s where s.event.eventId = :eventId AND s.seatNumber = :seatNumber")
  Optional<Seat> findByEventIdAndSeatNumberWithPessimisticLock(@Param("eventId") Long eventId,
      @Param("seatNumber") String seatNumber);

  Optional<Seat> findByEventIdAndSeatNumber(@Param("eventId") Long eventId, @Param("seatNumber") String seatNumber);

  @Modifying
  @Query("UPDATE Seat s SET s.status = :status, s.reservedUntil = :reservationTime, s.version = version + 1 WHERE s.seatId = :seatId AND s.version = :version")
  Integer updateSeatStatusWithVersion(@Param("seatId") Long seatId,
      @Param("reservationTime") String reservationTime, @Param("version") Long version, @Param("status") String status);
}
