package fr.fredos.dvdtheque.event.sourcing.demo.domain.model;

import java.util.List;

public interface EventStore {

    void store(String aggregateId, List<Event> newEvents, int baseVersion);
    List<Event> load(String aggregateId);
    List<Event> loadAllNotSentEvents();
    List<Event> loadAllEvents();
}
