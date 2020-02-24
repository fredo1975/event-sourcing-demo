package fr.fredos.dvdtheque.event.sourcing.demo.events.store;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeEntity;

public interface TradeRepository extends CrudRepository<TradeEntity, Long>{
	TradeEntity save(TradeEntity tradeEntity);
	@Query("SELECT tradeEntity FROM TradeEntity tradeEntity WHERE tradeEntity.aggregateIdentifier = ?1")
	List<TradeEntity> loadByAggregateId(String aggregateId);
}
