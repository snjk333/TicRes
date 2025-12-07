package com.oleksandr.eventprovider.rest;

import com.oleksandr.eventprovider.TicketMaster.EventReservationExternalService;
import com.oleksandr.eventprovider.TicketMaster.dto.ReserveTicketSimulation.ReservationRequestDto;
import com.oleksandr.eventprovider.TicketMaster.dto.ReserveTicketSimulation.ReservationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/external")
@Validated
public class EventReservationExternalController {

    private final EventReservationExternalService eventReservationExternalService;


    @PostMapping("/reserveTicket")
    public ResponseEntity<ReservationResponseDto> reserveTicket(@Valid @RequestBody ReservationRequestDto requestDto) {
        log.info("Received request to reserve ticket: {}", requestDto);
        ReservationResponseDto response = eventReservationExternalService.createExternalReservation(requestDto);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/cancelTicket")
    public ResponseEntity<ReservationResponseDto> cancelTicket(@Valid @RequestBody ReservationRequestDto requestDto) {
        log.info("Received request to cancel reservation: {}", requestDto);
        ReservationResponseDto response = eventReservationExternalService.cancelExternalReservation(requestDto);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/confirmTicket")
    public ResponseEntity<ReservationResponseDto> confirmTicket(@Valid @RequestBody ReservationRequestDto requestDto) {
        log.info("Received request to confirm reservation: {}", requestDto);
        ReservationResponseDto response = eventReservationExternalService.confirmExternalReservation(requestDto);
        return ResponseEntity.ok(response);
    }
}
