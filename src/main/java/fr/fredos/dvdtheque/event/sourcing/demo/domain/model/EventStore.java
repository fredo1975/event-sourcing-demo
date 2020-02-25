package fr.fredos.dvdtheque.event.sourcing.demo.domain.model;

import java.util.List;
import java.util.UUID;

import fr.fredos.dvdtheque.event.sourcing.demo.trade.service.SerializeException;

public interface EventStore {

    void store(UUID aggregateId, List<Event> newEvents, int baseVersion) throws SerializeException;
    List<Event> load(UUID aggregateId) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SerializeException;

}
