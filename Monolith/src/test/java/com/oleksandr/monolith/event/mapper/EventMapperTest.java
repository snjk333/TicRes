package com.oleksandr.monolith.event.mapper;

import com.oleksandr.common.dto.EventDTO;
import com.oleksandr.common.dto.TicketDTO;
import com.oleksandr.common.enums.TICKET_STATUS;
import com.oleksandr.monolith.event.model.Event;
import com.oleksandr.monolith.event.output.dto.EventDetailsDTO;
import com.oleksandr.monolith.event.output.dto.EventSummaryDTO;
import com.oleksandr.monolith.ticket.mapper.TicketMapper;
import com.oleksandr.monolith.ticket.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private EventMapper eventMapper;

    private UUID eventId;
    private UUID ticketId;
    private LocalDateTime eventDate;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        ticketId = UUID.randomUUID();
        eventDate = LocalDateTime.now().plusDays(7);
    }

    @Test
    void mapToEntity_shouldMapDTOToEvent() {
        // Given
        TicketDTO ticketDTO = TicketDTO.builder()
                .id(ticketId)
                .type("VIP")
                .price(100.00)
                .place("A1")
                .status(TICKET_STATUS.AVAILABLE)
                .build();

        EventDTO dto = EventDTO.builder()
                .id(eventId)
                .name("Concert")
                .description("Rock concert")
                .location("Stadium")
                .imageURL("http://image.url")
                .eventDate(eventDate)
                .tickets(List.of(ticketDTO))
                .build();

        Ticket mockTicket = new Ticket();
        mockTicket.setId(ticketId);
        when(ticketMapper.mapToEntity(any(TicketDTO.class))).thenReturn(mockTicket);

        // When
        Event result = eventMapper.mapToEntity(dto);

        // Then
        assertNotNull(result);
        assertEquals(eventId, result.getId());
        assertEquals("Concert", result.getName());
        assertEquals("Rock concert", result.getDescription());
        assertEquals("Stadium", result.getLocation());
        assertEquals("http://image.url", result.getImageURL());
        assertEquals(eventDate, result.getEventDate());
        assertNotNull(result.getTickets());
        assertEquals(1, result.getTickets().size());
        verify(ticketMapper, times(1)).mapToEntity(any(TicketDTO.class));
    }

    @Test
    void mapToEntity_shouldHandleNullTickets() {
        // Given
        EventDTO dto = EventDTO.builder()
                .id(eventId)
                .name("Concert")
                .description("Rock concert")
                .location("Stadium")
                .imageURL("http://image.url")
                .eventDate(eventDate)
                .tickets(null)
                .build();

        // When
        Event result = eventMapper.mapToEntity(dto);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTickets());
        assertTrue(result.getTickets().isEmpty());
    }

    @Test
    void mapToEntity_shouldThrowExceptionWhenDTOIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> eventMapper.mapToEntity(null));
    }

    @Test
    void mapToDto_shouldMapEventToDTO() {
        // Given
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setType("VIP");
        ticket.setPrice(100.00);
        ticket.setPlace("A1");
        ticket.setStatus(TICKET_STATUS.AVAILABLE);

        Event event = new Event();
        event.setId(eventId);
        event.setName("Concert");
        event.setDescription("Rock concert");
        event.setLocation("Stadium");
        event.setImageURL("http://image.url");
        event.setEventDate(eventDate);
        event.setTickets(List.of(ticket));

        TicketDTO ticketDTO = TicketDTO.builder()
                .id(ticketId)
                .type("VIP")
                .price(100.00)
                .place("A1")
                .status(TICKET_STATUS.AVAILABLE)
                .build();

        when(ticketMapper.mapEntityListToDtoList(anyList())).thenReturn(List.of(ticketDTO));

        // When
        EventDTO result = eventMapper.mapToDto(event);

        // Then
        assertNotNull(result);
        assertEquals(eventId, result.id());
        assertEquals("Concert", result.name());
        assertEquals("Rock concert", result.description());
        assertEquals("Stadium", result.location());
        assertEquals("http://image.url", result.imageURL());
        assertEquals(eventDate, result.eventDate());
        assertNotNull(result.tickets());
        assertEquals(1, result.tickets().size());
    }

    @Test
    void mapToDto_shouldHandleNullTickets() {
        // Given
        Event event = new Event();
        event.setId(eventId);
        event.setName("Concert");
        event.setDescription("Rock concert");
        event.setLocation("Stadium");
        event.setImageURL("http://image.url");
        event.setEventDate(eventDate);
        event.setTickets(null);

        // When
        EventDTO result = eventMapper.mapToDto(event);

        // Then
        assertNotNull(result);
        assertNotNull(result.tickets());
        assertTrue(result.tickets().isEmpty());
    }

    @Test
    void mapToDto_shouldThrowExceptionWhenEventIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> eventMapper.mapToDto(null));
    }

    @Test
    void mapListToDtoList_shouldMapListOfEvents() {
        // Given
        Event event1 = new Event();
        event1.setId(eventId);
        event1.setName("Concert 1");
        event1.setTickets(new ArrayList<>());

        Event event2 = new Event();
        event2.setId(UUID.randomUUID());
        event2.setName("Concert 2");
        event2.setTickets(new ArrayList<>());

        List<Event> events = Arrays.asList(event1, event2);

        when(ticketMapper.mapEntityListToDtoList(anyList())).thenReturn(List.of());

        // When
        List<EventDTO> result = eventMapper.mapListToDtoList(events);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(eventId, result.get(0).id());
        assertEquals("Concert 1", result.get(0).name());
    }

    @Test
    void mapListToDtoList_shouldReturnEmptyListWhenInputIsNull() {
        // When
        List<EventDTO> result = eventMapper.mapListToDtoList(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void mapToSummaryDto_shouldMapEventToSummaryDTO() {
        // Given
        Event event = new Event();
        event.setId(eventId);
        event.setName("Concert");
        event.setDescription("Rock concert");
        event.setLocation("Stadium");
        event.setImageURL("http://image.url");
        event.setEventDate(eventDate);

        // When
        EventSummaryDTO result = eventMapper.mapToSummaryDto(event);

        // Then
        assertNotNull(result);
        assertEquals(eventId, result.id());
        assertEquals("Concert", result.name());
        assertEquals("Rock concert", result.description());
        assertEquals("Stadium", result.location());
        assertEquals("http://image.url", result.imageURL());
        assertEquals(eventDate, result.eventDate());
    }

    @Test
    void mapToSummaryDto_shouldThrowExceptionWhenEventIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> eventMapper.mapToSummaryDto(null));
    }

    @Test
    void mapListToSummaryList_shouldMapListOfEventsToSummaryDTOs() {
        // Given
        Event event1 = new Event();
        event1.setId(eventId);
        event1.setName("Concert 1");
        event1.setDescription("Description 1");
        event1.setLocation("Location 1");
        event1.setImageURL("http://image1.url");
        event1.setEventDate(eventDate);

        Event event2 = new Event();
        event2.setId(UUID.randomUUID());
        event2.setName("Concert 2");
        event2.setDescription("Description 2");
        event2.setLocation("Location 2");
        event2.setImageURL("http://image2.url");
        event2.setEventDate(eventDate.plusDays(1));

        List<Event> events = Arrays.asList(event1, event2);

        // When
        List<EventSummaryDTO> result = eventMapper.mapListToSummaryList(events);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(eventId, result.get(0).id());
        assertEquals("Concert 1", result.get(0).name());
    }

    @Test
    void mapListToSummaryList_shouldReturnEmptyListWhenInputIsNull() {
        // When
        List<EventSummaryDTO> result = eventMapper.mapListToSummaryList(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void mapEventToDetailsDto_shouldMapEventToDetailsDTO() {
        // Given
        Event event = new Event();
        event.setId(eventId);
        event.setName("Concert");
        event.setDescription("Rock concert");
        event.setLocation("Stadium");
        event.setImageURL("http://image.url");
        event.setEventDate(eventDate);

        // When
        EventDetailsDTO result = eventMapper.mapEventToDetailsDto(event);

        // Then
        assertNotNull(result);
        assertEquals(eventId, result.id());
        assertEquals("Concert", result.name());
        assertEquals("Rock concert", result.description());
        assertEquals("Stadium", result.location());
        assertEquals("http://image.url", result.imageURL());
        assertEquals(eventDate, result.eventDate());
    }

    @Test
    void mapEventToDetailsDto_shouldThrowExceptionWhenEventIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> eventMapper.mapEventToDetailsDto(null));
    }

    @Test
    void updateEventInformation_shouldUpdateEventFields() {
        // Given
        Event event = new Event();
        event.setId(eventId);
        event.setName("Old Name");
        event.setDescription("Old Description");
        event.setLocation("Old Location");
        event.setImageURL("http://old-image.url");
        event.setEventDate(eventDate);
        event.setTickets(new ArrayList<>());

        EventDTO dto = EventDTO.builder()
                .name("New Name")
                .description("New Description")
                .location("New Location")
                .imageURL("http://new-image.url")
                .eventDate(eventDate.plusDays(1))
                .build();

        // When
        Event result = eventMapper.updateEventInformation(event, dto);

        // Then
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals("New Location", result.getLocation());
        assertEquals("http://new-image.url", result.getImageURL());
        assertEquals(eventDate.plusDays(1), result.getEventDate());
    }

    @Test
    void updateEventInformation_shouldReturnEventWhenDTOIsNull() {
        // Given
        Event event = new Event();
        event.setId(eventId);
        event.setName("Concert");

        // When
        Event result = eventMapper.updateEventInformation(event, null);

        // Then
        assertNotNull(result);
        assertEquals("Concert", result.getName());
    }

    @Test
    void mapToEntityForInsert_shouldMapDTOToEntityWithTickets() {
        // Given
        TicketDTO ticketDTO = TicketDTO.builder()
                .id(ticketId)
                .type("VIP")
                .price(100.00)
                .place("A1")
                .status(TICKET_STATUS.AVAILABLE)
                .build();

        EventDTO dto = EventDTO.builder()
                .id(eventId)
                .name("Concert")
                .description("Rock concert")
                .location("Stadium")
                .imageURL("http://image.url")
                .eventDate(eventDate)
                .tickets(List.of(ticketDTO))
                .build();

        Ticket mockTicket = new Ticket();
        mockTicket.setId(ticketId);
        when(ticketMapper.mapToEntity(any(TicketDTO.class))).thenReturn(mockTicket);

        // When
        Event result = eventMapper.mapToEntityForInsert(dto);

        // Then
        assertNotNull(result);
        assertEquals(eventId, result.getId());
        assertEquals("Concert", result.getName());
        assertNotNull(result.getTickets());
        assertEquals(1, result.getTickets().size());
    }

    @Test
    void mapToEntityForInsert_shouldThrowExceptionWhenDTOIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> eventMapper.mapToEntityForInsert(null));
    }

    @Test
    void updateEntityFromDto_shouldUpdateEventAndTickets() {
        // Given
        Ticket existingTicket = new Ticket();
        existingTicket.setId(ticketId);
        existingTicket.setType("VIP");
        existingTicket.setPrice(100.00);

        Event event = new Event();
        event.setId(eventId);
        event.setName("Old Name");
        event.setTickets(new ArrayList<>(List.of(existingTicket)));

        TicketDTO ticketDTO = TicketDTO.builder()
                .id(ticketId)
                .type("VIP Updated")
                .price(150.00)
                .place("A2")
                .status(TICKET_STATUS.AVAILABLE)
                .build();

        EventDTO dto = EventDTO.builder()
                .name("New Name")
                .description("New Description")
                .location("New Location")
                .imageURL("http://new-image.url")
                .eventDate(eventDate)
                .tickets(List.of(ticketDTO))
                .build();

        doNothing().when(ticketMapper).updateEntityFromDto(any(Ticket.class), any(TicketDTO.class));

        // When
        eventMapper.updateEntityFromDto(event, dto);

        // Then
        assertEquals("New Name", event.getName());
        assertEquals("New Description", event.getDescription());
        assertEquals("New Location", event.getLocation());
        assertEquals("http://new-image.url", event.getImageURL());
        assertEquals(eventDate, event.getEventDate());
        verify(ticketMapper, times(1)).updateEntityFromDto(any(Ticket.class), any(TicketDTO.class));
    }

    @Test
    void updateEntityFromDto_shouldAddNewTickets() {
        // Given
        Event event = new Event();
        event.setId(eventId);
        event.setName("Concert");
        event.setTickets(new ArrayList<>());

        TicketDTO newTicketDTO = TicketDTO.builder()
                .id(null)
                .type("Standard")
                .price(50.00)
                .place("B1")
                .status(TICKET_STATUS.AVAILABLE)
                .build();

        EventDTO dto = EventDTO.builder()
                .name("Concert")
                .description("Description")
                .location("Location")
                .imageURL("http://image.url")
                .eventDate(eventDate)
                .tickets(List.of(newTicketDTO))
                .build();

        Ticket mockTicket = new Ticket();
        when(ticketMapper.mapToEntity(any(TicketDTO.class))).thenReturn(mockTicket);

        // When
        eventMapper.updateEntityFromDto(event, dto);

        // Then
        assertEquals(1, event.getTickets().size());
        verify(ticketMapper, times(1)).mapToEntity(any(TicketDTO.class));
    }

    @Test
    void updateEntityFromDto_shouldHandleNullEntity() {
        // Given
        EventDTO dto = EventDTO.builder()
                .name("Concert")
                .build();

        // When & Then
        assertDoesNotThrow(() -> eventMapper.updateEntityFromDto(null, dto));
    }

    @Test
    void updateEntityFromDto_shouldHandleNullDTO() {
        // Given
        Event event = new Event();
        event.setId(eventId);
        event.setName("Concert");

        // When & Then
        assertDoesNotThrow(() -> eventMapper.updateEntityFromDto(event, null));
    }
}
