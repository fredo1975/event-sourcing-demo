package fr.fredos.dvdtheque.event.sourcing.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;

public interface TradeRepository extends CrudRepository<TradeEntity, Long>{
	List<Event> findByAggregateId(String aggregateId);
}
