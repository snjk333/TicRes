package com.oleksandr.monolith.user.model;

import com.oleksandr.common.enums.USER_ROLE;
import com.oleksandr.monolith.booking.model.Booking;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    //@GeneratedValue(generator = "UUID")
    private UUID id;

    private String username;
    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private USER_ROLE role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
}
