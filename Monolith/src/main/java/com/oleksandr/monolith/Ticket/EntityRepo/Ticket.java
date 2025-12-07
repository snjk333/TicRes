package com.oleksandr.monolith.Ticket.EntityRepo;

import com.oleksandr.monolith.Booking.EntityRepo.Booking;
import com.oleksandr.monolith.Event.EntityRepo.Event;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
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
    //@GeneratedValue(generator = "UUID")
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

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @Version
    @Column(nullable = false)
    private Long version;
}
