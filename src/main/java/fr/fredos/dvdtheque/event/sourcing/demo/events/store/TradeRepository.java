package fr.fredos.dvdtheque.event.sourcing.demo.events.store;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeEntity;

public interface TradeRepository extends CrudRepository<TradeEntity, Long>{
	TradeEntity save(TradeEntity tradeEntity);
	@Query("SELECT tradeEntity FROM TradeEntity tradeEntity WHERE tradeEntity.aggregateIdentifier = ?1")
	List<TradeEntity> loadByAggregateId(String aggregateId);
	/*
	@Query(value = "SELECT new TradeEntity(te.aggregateIdentifier as aggregateIdentifier,MAX(te.sequenceNumber) as sequenceNumber) FROM TradeEntity te join TradeEntity te2 on te2.aggregateIdentifier=te.aggregateIdentifier " + 
			"AND te2.payloadType<>'fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeSentEvent' GROUP BY te.aggregateIdentifier ",
			nativeQuery = true)*/
	@Query(value = "SELECT * " + 
			"FROM domain_event_entry INNER JOIN (" + 
			"SELECT MAX(sequence_number) AS m,aggregate_identifier as id FROM domain_event_entry GROUP BY aggregate_identifier) temp ON temp.id=aggregate_identifier " + 
			"AND sequence_number=m " + 
			"AND payload_type<>'fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeSentEvent' ",
			nativeQuery = true)
	List<TradeEntity> loadAllNotSentEvents();
	@Query("SELECT tradeEntity FROM TradeEntity tradeEntity")
	List<TradeEntity> loadAllEvents();
}
