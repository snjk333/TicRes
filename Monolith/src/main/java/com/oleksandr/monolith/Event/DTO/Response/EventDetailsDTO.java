package com.oleksandr.monolith.Event.DTO.Response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDetailsDTO {

    private UUID id;

    private String name;

    private String description;

    private String location;

    private String imageURL;

    private LocalDateTime eventDate;
}
