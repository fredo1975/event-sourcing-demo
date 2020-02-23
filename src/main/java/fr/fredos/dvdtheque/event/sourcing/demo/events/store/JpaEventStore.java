package fr.fredos.dvdtheque.event.sourcing.demo.events.store;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.EventStore;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.OptimisticLockingException;
import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeEntity;
import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeRepository;
@Component("jpaEventStore")
public class JpaEventStore implements EventStore{

	@Autowired
	TradeRepository tradeRepository;
	@Override
	public void store(UUID aggregateId, List<Event> newEvents, int baseVersion) throws OptimisticLockingException {
		TradeEntity entity = new TradeEntity(aggregateId.toString());
		tradeRepository.save(entity);
	}

	@Override
	public List<Event> load(UUID aggregateId) {
		return tradeRepository.findByAggregateId(aggregateId.toString());
	}

}
