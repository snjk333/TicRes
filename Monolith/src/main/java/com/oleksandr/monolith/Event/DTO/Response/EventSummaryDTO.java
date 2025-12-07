package com.oleksandr.monolith.Event.DTO.Response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventSummaryDTO {

    private UUID id;

    private String name;

    private String description;

    private String location;

    private String imageURL;

    private LocalDateTime eventDate;

}
