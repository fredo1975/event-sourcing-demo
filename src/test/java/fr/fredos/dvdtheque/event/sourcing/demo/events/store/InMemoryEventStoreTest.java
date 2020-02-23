package fr.fredos.dvdtheque.event.sourcing.demo.events.store;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.joda.time.DateTime.now;
import static org.joda.time.DateTimeZone.UTC;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.EventStore;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.OptimisticLockingException;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.event.sourcing.demo.EventSourcingSpringBootApplication.class})
class InMemoryEventStoreTest {
	@Autowired
    private EventStore inMemoryEventStore;

    @Test
    void storeEventsInOrder() {
        UUID aggregateId = randomUUID();
        Event e1 = new Event(aggregateId, now(UTC), 1){};
        Event e2 = new Event(aggregateId, now(UTC), 2){};
        Event e3 = new Event(aggregateId, now(UTC), 3){};
        inMemoryEventStore.store(aggregateId, newArrayList(e1), 0);
        inMemoryEventStore.store(aggregateId, newArrayList(e2), 1);
        inMemoryEventStore.store(aggregateId, newArrayList(e3), 2);

        List<Event> eventStream = inMemoryEventStore.load(aggregateId);
        assertThat(eventStream.size(), equalTo(3));
        assertThat(eventStream.get(0), equalTo(e1));
        assertThat(eventStream.get(1), equalTo(e2));
        assertThat(eventStream.get(2), equalTo(e3));
    }

    @Test
    void optimisticLocking() {
        UUID aggregateId = randomUUID();
        Event e1 = new Event(aggregateId, now(UTC), 1){};
        Event e2 = new Event(aggregateId, now(UTC), 2){};
        Event e3 = new Event(aggregateId, now(UTC), 2){};
        inMemoryEventStore.store(aggregateId, newArrayList(e1), 0);
        inMemoryEventStore.store(aggregateId, newArrayList(e2), 1);
        assertThrows(
            OptimisticLockingException.class,
            () -> inMemoryEventStore.store(aggregateId, newArrayList(e3), 1)
        );
    }

    @Test
    void loadedEventStreamIsImmutable() {
        UUID aggregateId = randomUUID();
        Event e1 = new Event(aggregateId, now(UTC), 1){};
        inMemoryEventStore.store(aggregateId, newArrayList(e1), 0);
        assertThrows(
            UnsupportedOperationException.class,
            () -> inMemoryEventStore.load(aggregateId).add(mock(Event.class))
        );
    }
}