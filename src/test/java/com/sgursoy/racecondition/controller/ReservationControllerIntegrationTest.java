package com.sgursoy.racecondition.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgursoy.racecondition.controller.request.SeatReservationRequest;
import com.sgursoy.racecondition.repository.EventRepository;
import com.sgursoy.racecondition.repository.ReservationRepository;
import com.sgursoy.racecondition.repository.SeatRepository;
import com.sgursoy.racecondition.repository.entity.Event;
import com.sgursoy.racecondition.repository.entity.Seat;
import com.sgursoy.racecondition.repository.entity.enums.EventStatus;
import com.sgursoy.racecondition.repository.entity.enums.SeatStatus;
import com.sgursoy.racecondition.repository.entity.enums.SeatType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("ReservationController Integration Tests")
public class ReservationControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Event testEvent;
    private Seat testSeat;

    @BeforeEach
    @Transactional
    void setUp() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clean up database
        reservationRepository.deleteAll();
        seatRepository.deleteAll();
        eventRepository.deleteAll();

        // Create test event
        testEvent = Event.builder()
                .eventName("Test Concert")
                .eventDate(LocalDateTime.now().plusDays(30))
                .totalSeats(100)
                .availableSeats(100)
                .status(EventStatus.ON_SALE)
                .saleStartTime(LocalDateTime.now().minusDays(1))
                .build();
        testEvent = eventRepository.save(testEvent);

        // Create test seat
        testSeat = Seat.builder()
                .event(testEvent)
                .seatNumber("A1")
                .seatType(SeatType.REGULAR)
                .price(new BigDecimal("100.00"))
                .status(SeatStatus.AVAILABLE)
                .version(0L)
                .build();
        testSeat = seatRepository.save(testSeat);
    }

    // ==================== Pessimistic Locking Tests ====================

    @Test
    @DisplayName("Pessimistic Locking - Successful Reservation")
    void testPessimisticLocking_Success() throws Exception {
        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user123")
                .eventId(testEvent.getEventId())
                .seatNumber("A1")
                .build();

        mockMvc.perform(get("/pessimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatId").value(testSeat.getSeatId()))
                .andExpect(jsonPath("$.reservationId").exists())
                .andExpect(jsonPath("$.errorCode").doesNotExist());

        // Verify seat is now reserved
        Seat updatedSeat = seatRepository.findById(testSeat.getSeatId()).orElseThrow();
        assertThat(updatedSeat.getStatus()).isEqualTo(SeatStatus.RESERVED);
        assertThat(updatedSeat.getReservedBy()).isEqualTo("user123");
    }

    @Test
    @DisplayName("Pessimistic Locking - Event Not Found")
    void testPessimisticLocking_EventNotFound() throws Exception {
        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user123")
                .eventId(99999L)
                .seatNumber("A1")
                .build();

        mockMvc.perform(get("/pessimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("EVENT_NOT_FOUND"));
    }

    @Test
    @DisplayName("Pessimistic Locking - Seat Not Found")
    void testPessimisticLocking_SeatNotFound() throws Exception {
        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user123")
                .eventId(testEvent.getEventId())
                .seatNumber("Z99")
                .build();

        mockMvc.perform(get("/pessimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("SEAT_NOT_FOUND"));
    }

    @Test
    @DisplayName("Pessimistic Locking - Seat Already Reserved")
    void testPessimisticLocking_SeatAlreadyReserved() throws Exception {
        // Reserve the seat first
        testSeat.setStatus(SeatStatus.RESERVED);
        testSeat.setReservedBy("user999");
        seatRepository.save(testSeat);

        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user123")
                .eventId(testEvent.getEventId())
                .seatNumber("A1")
                .build();

        mockMvc.perform(get("/pessimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("SEAT_NOT_AVAILABLE"));
    }

    // ==================== Optimistic Locking Tests ====================

    @Test
    @DisplayName("Optimistic Locking - Successful Reservation")
    void testOptimisticLocking_Success() throws Exception {
        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user456")
                .eventId(testEvent.getEventId())
                .seatNumber("A1")
                .build();

        mockMvc.perform(get("/optimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatId").value(testSeat.getSeatId()))
                .andExpect(jsonPath("$.reservationId").exists())
                .andExpect(jsonPath("$.errorCode").doesNotExist());

        // Verify seat is now reserved
        Seat updatedSeat = seatRepository.findById(testSeat.getSeatId()).orElseThrow();
        assertThat(updatedSeat.getStatus()).isEqualTo(SeatStatus.RESERVED);
        assertThat(updatedSeat.getReservedBy()).isEqualTo("user456");
    }

    @Test
    @DisplayName("Optimistic Locking - Seat Already Reserved")
    void testOptimisticLocking_SeatAlreadyReserved() throws Exception {
        // Reserve the seat first
        testSeat.setStatus(SeatStatus.RESERVED);
        testSeat.setReservedBy("user999");
        seatRepository.save(testSeat);

        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user456")
                .eventId(testEvent.getEventId())
                .seatNumber("A1")
                .build();

        mockMvc.perform(get("/optimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("SEAT_NOT_AVAILABLE"));
    }

    // ==================== Race Condition Tests ====================

    @Test
    @DisplayName("Pessimistic Locking - Prevents Race Condition with Concurrent Requests")
    void testPessimisticLocking_RaceCondition() throws Exception {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // All threads try to reserve the same seat
        for (int i = 0; i < numberOfThreads; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    SeatReservationRequest request = SeatReservationRequest.builder()
                            .userId("user" + userId)
                            .eventId(testEvent.getEventId())
                            .seatNumber("A1")
                            .build();

                    var result = mockMvc.perform(get("/pessimistic-locking")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andReturn();

                    if (result.getResponse().getStatus() == 200) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Only ONE thread should succeed
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(numberOfThreads - 1);

        // Verify only one reservation was created
        long reservationCount = reservationRepository.count();
        assertThat(reservationCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Optimistic Locking - Detects Concurrent Modifications")
    void testOptimisticLocking_ConcurrentModification() throws Exception {
        // Create multiple seats for this test
        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Seat seat = Seat.builder()
                    .event(testEvent)
                    .seatNumber("B" + i)
                    .seatType(SeatType.REGULAR)
                    .price(new BigDecimal("100.00"))
                    .status(SeatStatus.AVAILABLE)
                    .version(0L)
                    .build();
            seats.add(seatRepository.save(seat));
        }

        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger concurrentModificationCount = new AtomicInteger(0);

        // All threads try to reserve the first seat
        for (int i = 0; i < numberOfThreads; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    SeatReservationRequest request = SeatReservationRequest.builder()
                            .userId("user" + userId)
                            .eventId(testEvent.getEventId())
                            .seatNumber("B0")
                            .build();

                    var result = mockMvc.perform(get("/optimistic-locking")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andReturn();

                    String responseBody = result.getResponse().getContentAsString();

                    if (result.getResponse().getStatus() == 200) {
                        successCount.incrementAndGet();
                    } else if (responseBody.contains("CONCURRENT_MODIFICATION") ||
                            responseBody.contains("SEAT_NOT_AVAILABLE")) {
                        concurrentModificationCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    concurrentModificationCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // At least one should succeed, others should fail with concurrent modification
        // or seat not available
        assertThat(successCount.get()).isGreaterThanOrEqualTo(1);
        assertThat(successCount.get() + concurrentModificationCount.get()).isEqualTo(numberOfThreads);
    }

    @Test
    @DisplayName("Integration - Multiple Users Reserving Different Seats")
    void testMultipleUsersReservingDifferentSeats() throws Exception {
        // Create multiple seats
        List<Seat> seats = new ArrayList<>();
        for (int i = 2; i <= 5; i++) {
            Seat seat = Seat.builder()
                    .event(testEvent)
                    .seatNumber("C" + i)
                    .seatType(SeatType.REGULAR)
                    .price(new BigDecimal("100.00"))
                    .status(SeatStatus.AVAILABLE)
                    .version(0L)
                    .build();
            seats.add(seatRepository.save(seat));
        }

        int numberOfThreads = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        // Each thread reserves a different seat
        for (int i = 0; i < numberOfThreads; i++) {
            final int seatIndex = i;
            executorService.submit(() -> {
                try {
                    SeatReservationRequest request = SeatReservationRequest.builder()
                            .userId("user" + seatIndex)
                            .eventId(testEvent.getEventId())
                            .seatNumber("C" + (seatIndex + 2))
                            .build();

                    var result = mockMvc.perform(get("/pessimistic-locking")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andReturn();

                    if (result.getResponse().getStatus() == 200) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Ignore
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // All should succeed since they're reserving different seats
        assertThat(successCount.get()).isEqualTo(numberOfThreads);
        assertThat(reservationRepository.count()).isEqualTo(numberOfThreads);
    }
}
