package com.oleksandr.monolith.event.output.dto;

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
