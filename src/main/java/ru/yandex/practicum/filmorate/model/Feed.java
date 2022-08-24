package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "eventId")
@Builder
public class Feed {
    private Long eventId;
    private Long userId;
    private long timestamp = Instant.now().toEpochMilli();
    private EventType eventType;
    private Operation operation;
    private Long entityId;

    public Feed(Long userId, EventType eventType, Operation operation, Long entityId) {
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}