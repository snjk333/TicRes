package com.oleksandr.monolith.ticket.mapper;

import com.oleksandr.common.dto.TicketDTO;
import com.oleksandr.common.enums.TICKET_STATUS;
import com.oleksandr.monolith.event.model.Event;
import com.oleksandr.monolith.ticket.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TicketMapperTest {

    private TicketMapper ticketMapper;

    @BeforeEach
    void setUp() {
        ticketMapper = new TicketMapper();
    }

    @Test
    void mapToEntity_shouldMapDTOToTicket() {
        // Given
        UUID ticketId = UUID.randomUUID();
        TicketDTO dto = TicketDTO.builder()
                .id(ticketId)
                .type("VIP")
                .price(100.00)
                .place("A1")
                .status(TICKET_STATUS.AVAILABLE)
                .build();

        // When
        Ticket result = ticketMapper.mapToEntity(dto);

        // Then
        assertNotNull(result);
        assertEquals(ticketId, result.getId());
        assertEquals("VIP", result.getType());
        assertEquals(100.00, result.getPrice());
        assertEquals("A1", result.getPlace());
        assertEquals(TICKET_STATUS.AVAILABLE, result.getStatus());
    }

    @Test
    void mapToEntity_shouldSetDefaultStatusWhenNull() {
        // Given
        UUID ticketId = UUID.randomUUID();
        TicketDTO dto = TicketDTO.builder()
                .id(ticketId)
                .type("Standard")
                .price(50.00)
                .place("B5")
                .status(null)
                .build();

        // When
        Ticket result = ticketMapper.mapToEntity(dto);

        // Then
        assertNotNull(result);
        assertEquals(TICKET_STATUS.AVAILABLE, result.getStatus());
    }

    @Test
    void mapToEntity_shouldThrowExceptionWhenDTOIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> ticketMapper.mapToEntity(null));
    }

    @Test
    void mapTicketsListFromDto_shouldMapListOfDTOs() {
        // Given
        UUID ticketId1 = UUID.randomUUID();
        UUID ticketId2 = UUID.randomUUID();

        TicketDTO dto1 = TicketDTO.builder()
                .id(ticketId1)
                .type("VIP")
                .price(100.00)
                .place("A1")
                .status(TICKET_STATUS.AVAILABLE)
                .build();

        TicketDTO dto2 = TicketDTO.builder()
                .id(ticketId2)
                .type("Standard")
                .price(50.00)
                .place("B5")
                .status(TICKET_STATUS.RESERVED)
                .build();

        List<TicketDTO> dtos = Arrays.asList(dto1, dto2);

        // When
        List<Ticket> result = ticketMapper.mapTicketsListFromDto(dtos);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ticketId1, result.get(0).getId());
        assertEquals(ticketId2, result.get(1).getId());
    }

    @Test
    void mapTicketsListFromDto_shouldReturnEmptyListWhenInputIsNull() {
        // When
        List<Ticket> result = ticketMapper.mapTicketsListFromDto(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void mapTicketsListFromDto_shouldSkipNullElements() {
        // Given
        UUID ticketId = UUID.randomUUID();
        TicketDTO dto = TicketDTO.builder()
                .id(ticketId)
                .type("VIP")
                .price(100.00)
                .place("A1")
                .status(TICKET_STATUS.AVAILABLE)
                .build();

        List<TicketDTO> dtos = Arrays.asList(dto, null);

        // When
        List<Ticket> result = ticketMapper.mapTicketsListFromDto(dtos);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ticketId, result.get(0).getId());
    }

    @Test
    void mapToDto_shouldMapTicketToDTO() {
        // Given
        UUID ticketId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        Event event = new Event();
        event.setId(eventId);

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setEvent(event);
        ticket.setType("VIP");
        ticket.setPrice(100.00);
        ticket.setPlace("A1");
        ticket.setStatus(TICKET_STATUS.AVAILABLE);

        // When
        TicketDTO result = ticketMapper.mapToDto(ticket);

        // Then
        assertNotNull(result);
        assertEquals(ticketId, result.id());
        assertEquals(eventId, result.eventId());
        assertEquals("VIP", result.type());
        assertEquals(100.00, result.price());
        assertEquals("A1", result.place());
        assertEquals(TICKET_STATUS.AVAILABLE, result.status());
    }

    @Test
    void mapToDto_shouldHandleNullEvent() {
        // Given
        UUID ticketId = UUID.randomUUID();

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setEvent(null);
        ticket.setType("Standard");
        ticket.setPrice(50.00);
        ticket.setPlace("B5");
        ticket.setStatus(TICKET_STATUS.RESERVED);

        // When
        TicketDTO result = ticketMapper.mapToDto(ticket);

        // Then
        assertNotNull(result);
        assertEquals(ticketId, result.id());
        assertNull(result.eventId());
        assertEquals("Standard", result.type());
    }

    @Test
    void mapToDto_shouldThrowExceptionWhenTicketIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> ticketMapper.mapToDto(null));
    }

    @Test
    void mapEntityListToDtoList_shouldMapListOfTickets() {
        // Given
        UUID ticketId1 = UUID.randomUUID();
        UUID ticketId2 = UUID.randomUUID();

        Ticket ticket1 = new Ticket();
        ticket1.setId(ticketId1);
        ticket1.setType("VIP");
        ticket1.setPrice(100.00);
        ticket1.setPlace("A1");
        ticket1.setStatus(TICKET_STATUS.AVAILABLE);

        Ticket ticket2 = new Ticket();
        ticket2.setId(ticketId2);
        ticket2.setType("Standard");
        ticket2.setPrice(50.00);
        ticket2.setPlace("B5");
        ticket2.setStatus(TICKET_STATUS.RESERVED);

        List<Ticket> tickets = Arrays.asList(ticket1, ticket2);

        // When
        List<TicketDTO> result = ticketMapper.mapEntityListToDtoList(tickets);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ticketId1, result.get(0).id());
        assertEquals(ticketId2, result.get(1).id());
    }

    @Test
    void mapEntityListToDtoList_shouldReturnEmptyListWhenInputIsNull() {
        // When
        List<TicketDTO> result = ticketMapper.mapEntityListToDtoList(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void updateEntityFromDto_shouldUpdateTicketFields() {
        // Given
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setType("Old Type");
        ticket.setPrice(50.00);
        ticket.setPlace("A1");
        ticket.setStatus(TICKET_STATUS.AVAILABLE);

        TicketDTO dto = TicketDTO.builder()
                .type("New Type")
                .price(100.00)
                .place("B5")
                .status(TICKET_STATUS.RESERVED)
                .build();

        // When
        ticketMapper.updateEntityFromDto(ticket, dto);

        // Then
        assertEquals("New Type", ticket.getType());
        assertEquals(100.00, ticket.getPrice());
        assertEquals("B5", ticket.getPlace());
        assertEquals(TICKET_STATUS.RESERVED, ticket.getStatus());
    }

    @Test
    void updateEntityFromDto_shouldKeepExistingStatusWhenDTOStatusIsNull() {
        // Given
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setType("VIP");
        ticket.setPrice(100.00);
        ticket.setPlace("A1");
        ticket.setStatus(TICKET_STATUS.RESERVED);

        TicketDTO dto = TicketDTO.builder()
                .type("Updated Type")
                .price(150.00)
                .place("A2")
                .status(null)
                .build();

        // When
        ticketMapper.updateEntityFromDto(ticket, dto);

        // Then
        assertEquals("Updated Type", ticket.getType());
        assertEquals(150.00, ticket.getPrice());
        assertEquals("A2", ticket.getPlace());
        assertEquals(TICKET_STATUS.RESERVED, ticket.getStatus());
    }

    @Test
    void updateEntityFromDto_shouldHandleNullEntity() {
        // Given
        TicketDTO dto = TicketDTO.builder()
                .type("VIP")
                .price(100.00)
                .place("A1")
                .status(TICKET_STATUS.AVAILABLE)
                .build();

        // When & Then
        assertDoesNotThrow(() -> ticketMapper.updateEntityFromDto(null, dto));
    }

    @Test
    void updateEntityFromDto_shouldHandleNullDTO() {
        // Given
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setType("VIP");
        ticket.setPrice(100.00);
        ticket.setPlace("A1");
        ticket.setStatus(TICKET_STATUS.AVAILABLE);

        String originalType = ticket.getType();

        // When
        ticketMapper.updateEntityFromDto(ticket, null);

        // Then
        assertEquals(originalType, ticket.getType());
    }
}
