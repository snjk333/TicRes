package com.oleksandr.monolith.Event.DTO.Response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;


@Builder
public record EventDetailsDTO (
        UUID id,
        String name,
        String description,
        String location,
        String imageURL,
        LocalDateTime eventDate
){ }
