package com.oleksandr.eventprovider.TicketMaster.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ImageDto(
         String url,
         String ratio,
         Integer width,
         Integer height) { }