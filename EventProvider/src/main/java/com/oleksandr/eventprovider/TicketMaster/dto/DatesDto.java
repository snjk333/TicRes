package com.oleksandr.eventprovider.TicketMaster.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatesDto(StartDateDto start) { }