package com.sgursoy.racecondition.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgursoy.racecondition.controller.TicketingController;
import com.sgursoy.racecondition.controller.request.SeatReservationRequest;
import com.sgursoy.racecondition.controller.response.SeatReservationResponse;
import com.sgursoy.racecondition.service.ErrorCodes;
import com.sgursoy.racecondition.service.TicketingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("TicketingController Tests")
public class TicketingControllerTest {

    @Mock
    private TicketingService ticketingService;

    @InjectMocks
    private TicketingController ticketingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketingController).build();
        objectMapper = new ObjectMapper();
    }

    // ==================== Pessimistic Locking Tests ====================

    @Test
    @DisplayName("Pessimistic Locking - Success")
    void testPessimisticLocking_Success() throws Exception {
        // Arrange
        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user123")
                .eventId(1L)
                .seatNumber("A1")
                .build();

        SeatReservationResponse response = new SeatReservationResponse()
                .setSeatId(100L)
                .setReservationId(200L);

        when(ticketingService.makeReservationForUserWithPessimisticLocking(any(SeatReservationRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/pessimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatId").value(100L))
                .andExpect(jsonPath("$.reservationId").value(200L))
                .andExpect(jsonPath("$.errorCode").doesNotExist());
    }

    @Test
    @DisplayName("Pessimistic Locking - Event Not Found")
    void testPessimisticLocking_EventNotFound() throws Exception {
        // Arrange
        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user123")
                .eventId(999L)
                .seatNumber("A1")
                .build();

        SeatReservationResponse response = new SeatReservationResponse()
                .setErrorCode(ErrorCodes.EVENT_NOT_FOUND);

        when(ticketingService.makeReservationForUserWithPessimisticLocking(any(SeatReservationRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/pessimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("EVENT_NOT_FOUND"));
    }

    @Test
    @DisplayName("Pessimistic Locking - Seat Not Found")
    void testPessimisticLocking_SeatNotFound() throws Exception {
        // Arrange
        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user123")
                .eventId(1L)
                .seatNumber("Z99")
                .build();

        SeatReservationResponse response = new SeatReservationResponse()
                .setErrorCode(ErrorCodes.SEAT_NOT_FOUND);

        when(ticketingService.makeReservationForUserWithPessimisticLocking(any(SeatReservationRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/pessimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("SEAT_NOT_FOUND"));
    }

    @Test
    @DisplayName("Pessimistic Locking - Seat Not Available")
    void testPessimisticLocking_SeatNotAvailable() throws Exception {
        // Arrange
        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user123")
                .eventId(1L)
                .seatNumber("A1")
                .build();

        SeatReservationResponse response = new SeatReservationResponse()
                .setErrorCode(ErrorCodes.SEAT_NOT_AVAILABLE);

        when(ticketingService.makeReservationForUserWithPessimisticLocking(any(SeatReservationRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/pessimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("SEAT_NOT_AVAILABLE"));
    }

    // ==================== Optimistic Locking Tests ====================

    @Test
    @DisplayName("Optimistic Locking - Success")
    void testOptimisticLocking_Success() throws Exception {
        // Arrange
        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user456")
                .eventId(2L)
                .seatNumber("B5")
                .build();

        SeatReservationResponse response = new SeatReservationResponse()
                .setSeatId(150L)
                .setReservationId(250L);

        when(ticketingService.makeReservationForUserWithOptimisticLocking(any(SeatReservationRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/optimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatId").value(150L))
                .andExpect(jsonPath("$.reservationId").value(250L))
                .andExpect(jsonPath("$.errorCode").doesNotExist());
    }

    @Test
    @DisplayName("Optimistic Locking - Event Not Found")
    void testOptimisticLocking_EventNotFound() throws Exception {
        // Arrange
        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user456")
                .eventId(999L)
                .seatNumber("B5")
                .build();

        SeatReservationResponse response = new SeatReservationResponse()
                .setErrorCode(ErrorCodes.EVENT_NOT_FOUND);

        when(ticketingService.makeReservationForUserWithOptimisticLocking(any(SeatReservationRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/optimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("EVENT_NOT_FOUND"));
    }

    @Test
    @DisplayName("Optimistic Locking - Seat Not Found")
    void testOptimisticLocking_SeatNotFound() throws Exception {
        // Arrange
        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user456")
                .eventId(2L)
                .seatNumber("Z99")
                .build();

        SeatReservationResponse response = new SeatReservationResponse()
                .setErrorCode(ErrorCodes.SEAT_NOT_FOUND);

        when(ticketingService.makeReservationForUserWithOptimisticLocking(any(SeatReservationRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/optimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("SEAT_NOT_FOUND"));
    }

    @Test
    @DisplayName("Optimistic Locking - Seat Not Available")
    void testOptimisticLocking_SeatNotAvailable() throws Exception {
        // Arrange
        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user456")
                .eventId(2L)
                .seatNumber("B5")
                .build();

        SeatReservationResponse response = new SeatReservationResponse()
                .setErrorCode(ErrorCodes.SEAT_NOT_AVAILABLE);

        when(ticketingService.makeReservationForUserWithOptimisticLocking(any(SeatReservationRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/optimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("SEAT_NOT_AVAILABLE"));
    }

    @Test
    @DisplayName("Optimistic Locking - Concurrent Modification")
    void testOptimisticLocking_ConcurrentModification() throws Exception {
        // Arrange
        SeatReservationRequest request = SeatReservationRequest.builder()
                .userId("user456")
                .eventId(2L)
                .seatNumber("B5")
                .build();

        SeatReservationResponse response = new SeatReservationResponse()
                .setErrorCode(ErrorCodes.CONCURRENT_MODIFICATION);

        when(ticketingService.makeReservationForUserWithOptimisticLocking(any(SeatReservationRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/optimistic-locking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("CONCURRENT_MODIFICATION"));
    }
}
