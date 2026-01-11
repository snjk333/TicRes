package com.oleksandr.monolith.event.service.impl;

import com.oleksandr.common.dto.EventDTO;
import com.oleksandr.monolith.event.mapper.EventMapper;
import com.oleksandr.monolith.event.model.Event;
import com.oleksandr.monolith.event.repository.EventRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSyncService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EntityManager entityManager;

    @Transactional
    public void syncAll(List<EventDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return;

        Map<UUID, EventDTO> unique = dtos.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(EventDTO::id, d -> d, (a, b)->a));

        for (EventDTO dto : unique.values()) {
            if (dto == null || dto.id() == null) continue;

            if (eventRepository.existsById(dto.id())) {
                Event existing = eventRepository.findById(dto.id()).orElseThrow();
                eventMapper.updateEntityFromDto(existing, dto);
                // managed — Hibernate
            } else {
                Event toInsert = eventMapper.mapToEntityForInsert(dto);
                try {
                    entityManager.persist(toInsert);
                    entityManager.flush();
                } catch (PersistenceException ex) {
                    // race:  exists and persist
                    Event existing = eventRepository.findById(dto.id()).orElse(null);
                    if (existing != null) {
                        eventMapper.updateEntityFromDto(existing, dto);
                    } else {
                        throw ex;
                    }
                }
            }
        }
    }
}

