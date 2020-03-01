package fr.fredos.dvdtheque.event.sourcing.demo.domain.model;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Event {

    private final String aggregateId;
    private final int version;

    public Event(String aggregateId, int version) {
        this.aggregateId = checkNotNull(aggregateId);
        this.version = checkNotNull(version);
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public int getVersion() {
        return version;
    }
}
