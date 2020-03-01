package fr.fredos.dvdtheque.event.sourcing.demo.events.store;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.EventStore;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.OptimisticLockingException;

@SpringBootTest(classes = {fr.fredos.dvdtheque.event.sourcing.demo.events.store.JpaEventStore.class,fr.fredos.dvdtheque.event.sourcing.demo.EventSourcingSpringBootApplication.class})
@ActiveProfiles("jpa")
public class JpaEventStoreTest {
	@Autowired
    private EventStore jpaEventStore;
	
	@Test
    void storeEventsInOrder() throws ClassNotFoundException, InstantiationException, IllegalAccessException, OptimisticLockingException, JsonProcessingException {
        UUID aggregateId = randomUUID();
        Event e1 = new Event(aggregateId.toString(),1){};
        Event e2 = new Event(aggregateId.toString(),2){};
        Event e3 = new Event(aggregateId.toString(),3){};
        jpaEventStore.store(aggregateId.toString(), newArrayList(e1), 0);
        jpaEventStore.store(aggregateId.toString(), newArrayList(e2), 1);
        jpaEventStore.store(aggregateId.toString(), newArrayList(e3), 2);

        List<Event> eventStream = jpaEventStore.load(aggregateId.toString());
        assertThat(eventStream.size(), equalTo(3));
        assertThat(eventStream.get(0), equalTo(e1));
        assertThat(eventStream.get(1), equalTo(e2));
        assertThat(eventStream.get(2), equalTo(e3));
    }
}
