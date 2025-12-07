package com.oleksandr.eventprovider.TicketMaster.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TicketmasterResponse(
        @JsonProperty("_embedded")
        EmbeddedData embedded

) { }