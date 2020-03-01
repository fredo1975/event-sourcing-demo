package fr.fredos.dvdtheque.event.sourcing.demo.events.store;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.EventStore;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.OptimisticLockingException;
@Component("inMemoryEventStore")
public class InMemoryEventStore implements EventStore {

    private final Map<String, List<Event>> eventStore = new ConcurrentHashMap<>();

    @Override
    public void store(String aggregateId, List<Event> newEvents, int baseVersion) throws OptimisticLockingException {
        eventStore.merge(aggregateId, newEvents, (oldValue, value) -> {
            if (baseVersion != oldValue.get(oldValue.size() - 1).getVersion())
                throw new OptimisticLockingException("Base version does not match current stored version");

            return Stream.concat(oldValue.stream(), value.stream()).collect(toList());
        });
    }

    @Override
    public List<Event> load(String aggregateId) {
        return ImmutableList.copyOf(eventStore.getOrDefault(aggregateId, emptyList()));
    }

	@Override
	public List<Event> loadAllNotSentEvents() {
		// TODO Auto-generated method stub
		return null;
	}
}
