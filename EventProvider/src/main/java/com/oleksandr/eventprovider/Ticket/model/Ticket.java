package com.oleksandr.eventprovider.Ticket.model;

import com.oleksandr.common.enums.TICKET_STATUS;
import com.oleksandr.eventprovider.event.model.Event;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Ticket {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    private String type;
    private double price;

    private String place;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TICKET_STATUS status;

}
