package com.oleksandr.monolith.Event.EntityRepo;

import com.oleksandr.monolith.Ticket.EntityRepo.Ticket;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Event {

    @Id
    //@GeneratedValue(generator = "UUID")
    private UUID id;

    private String name;
    private String description;
    private String location;
    private LocalDateTime eventDate;
    private String imageURL;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();

    @Version
    @Column(nullable = false)
    private Long version;

    public Event(UUID id, String name, String description, String location, LocalDateTime eventDate, String imageURL, List<Ticket> tickets) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.eventDate = eventDate;
        this.imageURL = imageURL;
        this.tickets = tickets;
    }
}
