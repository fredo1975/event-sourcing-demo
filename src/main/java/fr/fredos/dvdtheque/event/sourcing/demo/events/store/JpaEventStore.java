package fr.fredos.dvdtheque.event.sourcing.demo.events.store;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.EventStore;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.OptimisticLockingException;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeCfinRetrievedEvent;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeReceivedEvent;
import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeEntity;
@Component("jpaEventStore")
public class JpaEventStore implements EventStore{

	@Autowired
	TradeRepository tradeRepository;
	
	@Override
	public void store(UUID aggregateId, List<Event> newEvents, int baseVersion) throws OptimisticLockingException, JsonProcessingException {
		TradeEntity entity = new TradeEntity(aggregateId.toString());
		entity.setSequenceNumber(Integer.valueOf(baseVersion));
		entity.setAggregateIdentifier(aggregateId.toString());
		entity.setEventIdentifier(randomUUID().toString());
		Event event = newEvents.get(0);
		ObjectMapper map = new ObjectMapper();
		if(event instanceof TradeReceivedEvent) {
			TradeReceivedEvent e = (TradeReceivedEvent)event;
			entity.setPayload(map.writeValueAsString(e));
		}else {
			TradeCfinRetrievedEvent e = (TradeCfinRetrievedEvent)event;
			entity.setPayload(map.writeValueAsString(e));
		}
		entity.setPayloadType(event.getClass().getTypeName());
		tradeRepository.save(entity);
		//em.getTransaction().commit();
	}

	@Override
	public List<Event> load(UUID aggregateId) throws ClassNotFoundException, InstantiationException, IllegalAccessException, JsonMappingException, JsonProcessingException {
		List<TradeEntity> l = tradeRepository.loadByAggregateId(aggregateId.toString());
		if(CollectionUtils.isNotEmpty(l)) {
			List<Event> eventList = new ArrayList<Event>();
			ObjectMapper map = new ObjectMapper();
			for(TradeEntity te : l) {
				Class<?> clazz = Class.forName(te.getPayloadType());
				if(clazz.equals(TradeReceivedEvent.class)) {
					TradeReceivedEvent e = (TradeReceivedEvent) map.readValue(te.getPayload(), clazz);
					//TradeReceivedEvent e = (TradeReceivedEvent) clazz.newInstance();
					eventList.add(e);
				}else {
					TradeCfinRetrievedEvent e = (TradeCfinRetrievedEvent) clazz.newInstance();
					eventList.add(e);
				}
			}
			return eventList;
		}
		return emptyList();
	}

}
