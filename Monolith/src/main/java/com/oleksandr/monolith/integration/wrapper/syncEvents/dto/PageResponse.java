package com.oleksandr.monolith.integration.wrapper.syncEvents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> content;
    
    @JsonProperty("totalPages")
    private int totalPages;
    
    @JsonProperty("totalElements")
    private long totalElements;
    
    @JsonProperty("size")
    private int size;
    
    @JsonProperty("number")
    private int number;
    
    @JsonProperty("numberOfElements")
    private int numberOfElements;
    
    @JsonProperty("first")
    private boolean first;
    
    @JsonProperty("last")
    private boolean last;
    
    @JsonProperty("empty")
    private boolean empty;
}
