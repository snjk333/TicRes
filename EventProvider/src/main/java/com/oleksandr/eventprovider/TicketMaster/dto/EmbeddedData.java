package com.oleksandr.eventprovider.TicketMaster.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EmbeddedData(

        List<EventMasterDto> events

) {}
