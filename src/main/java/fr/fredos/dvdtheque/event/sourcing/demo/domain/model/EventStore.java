package fr.fredos.dvdtheque.event.sourcing.demo.domain.model;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface EventStore {

    void store(UUID aggregateId, List<Event> newEvents, int baseVersion) throws OptimisticLockingException, JsonProcessingException;
    List<Event> load(UUID aggregateId) throws ClassNotFoundException, InstantiationException, IllegalAccessException, JsonMappingException, JsonProcessingException;

}
