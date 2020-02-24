package fr.fredos.dvdtheque.event.sourcing.demo.domain.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

public abstract class Event {

    private final UUID aggregateId;
    private final int version;

    public Event(UUID aggregateId, int version) {
        this.aggregateId = checkNotNull(aggregateId);
        this.version = checkNotNull(version);
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public int getVersion() {
        return version;
    }
}
