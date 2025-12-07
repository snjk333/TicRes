package com.oleksandr.eventprovider.FakeInfo;

import com.oleksandr.eventprovider.Event.Event;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeRepository {

    public List<Event> events;

    public FakeRepository() {
        events = new ArrayList<>();

        events.add(new Event(
                UUID.randomUUID(),
                "fake-rock-fest-2025",
                "Rock Fest 2025",
                "Annual open-air rock music festival with top European bands.",
                "Warsaw National Stadium",
                LocalDateTime.of(2025, 7, 12, 18, 0),
                "https://www.mitko.pl/wp-content/uploads/2025/02/co-to-jest-event-i-jakie-sa-rodzaje.jpg",
                new ArrayList<>()
        ));

        events.add(new Event(
                UUID.randomUUID(),
                "fake-tech-conf-2025", // externalId
                "Tech Conference 2025",
                "International IT conference covering AI, Cloud and Security.",
                "Kraków Expo Center",
                LocalDateTime.of(2025, 9, 21, 9, 0),
                "https://www.mitko.pl/wp-content/uploads/2025/02/co-to-jest-event-i-jakie-sa-rodzaje.jpg",
                new ArrayList<>()
        ));

        events.add(new Event(
                UUID.randomUUID(),
                "fake-classical-2025", // externalId
                "Classical Evening",
                "Concert of symphonic orchestra performing Mozart and Beethoven.",
                "Poznań Philharmonic Hall",
                LocalDateTime.of(2025, 10, 3, 19, 30),
                "https://www.mitko.pl/wp-content/uploads/2025/02/co-to-jest-event-i-jakie-sa-rodzaje.jpg",
                new ArrayList<>()
        ));

        events.add(new Event(
                UUID.randomUUID(),
                "fake-standup-2025", // externalId
                "Stand-up Comedy Night",
                "Evening of comedy with popular stand-up artists from Poland.",
                "Wrocław Comedy Club",
                LocalDateTime.of(2025, 11, 15, 20, 0),
                "https://www.mitko.pl/wp-content/uploads/2025/02/co-to-jest-event-i-jakie-sa-rodzaje.jpg",
                new ArrayList<>()
        ));

        events.add(new Event(
                UUID.randomUUID(),
                "fake-xmas-market-2025", // externalId
                "Christmas Market",
                "Traditional Christmas fair with food, crafts, and entertainment.",
                "Gdańsk Old Town",
                LocalDateTime.of(2025, 12, 10, 16, 0),
                "https://www.mitko.pl/wp-content/uploads/2025/02/co-to-jest-event-i-jakie-sa-rodzaje.jpg",
                new ArrayList<>()
        ));
    }

    public List<Event> getAllEvents() {
        return events;
    }

    public Event findById(UUID id) {
        return events.stream().filter(event -> event.getId().equals(id)).findFirst().orElse(null);
    }
}
