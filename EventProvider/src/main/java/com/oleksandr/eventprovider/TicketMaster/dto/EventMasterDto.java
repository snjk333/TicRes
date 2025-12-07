package com.oleksandr.eventprovider.TicketMaster.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record EventMasterDto(

        String id,
        String name,
        List<ImageDto> images,
        DatesDto dates,

        @JsonProperty("_embedded")
        EmbeddedData eventEmbedded

) {}