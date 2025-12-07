package com.oleksandr.monolith.Event.DTO.Response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record EventSummaryDTO (

        UUID id,

        String name,

        String description,

        String location,

        String imageURL,

        LocalDateTime eventDate

){ }
