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
    private int eventId;
    private int userId;
    private long timestamp = Instant.now().toEpochMilli();
    private String eventType;
    private String operation;
    private int entityId;

    public Feed(int userId, String eventType, String operation, int entityId) {
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}