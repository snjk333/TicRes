package com.oleksandr.monolith.Event.DTO;

import com.oleksandr.monolith.Ticket.DTO.TicketDTO;
import jakarta.validation.Valid;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDTO {

    private UUID id;

    @NotBlank(message = "Event name cannot be blank")
    private String name;

    @NotBlank(message = "Event description cannot be blank")
    private String description;

    @NotBlank(message = "Event location cannot be blank")
    private String location;

    private String imageURL;

    @NotNull(message = "Event date cannot be null")
    private LocalDateTime eventDate;

    @Valid
    private List<TicketDTO> tickets;
}
