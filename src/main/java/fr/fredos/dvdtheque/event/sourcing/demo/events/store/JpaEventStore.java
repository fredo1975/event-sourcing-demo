package fr.fredos.dvdtheque.event.sourcing.demo.events.store;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.EventStore;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeCfinRetrievedEvent;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeReceivedEvent;
import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeEntity;
import fr.fredos.dvdtheque.event.sourcing.demo.trade.service.SerializeException;
@Component("jpaEventStore")
public class JpaEventStore implements EventStore{

	@Autowired
	TradeRepository tradeRepository;
	private IMap<String, List<TradeEntity>> mapEvents;
	@Autowired
	private HazelcastInstance instance;
	@PostConstruct
	public void init() {
		mapEvents = instance.getMap("events");
	}
	
	
	@Override
	public void store(UUID aggregateId, List<Event> newEvents, int baseVersion) throws SerializeException {
		TradeEntity entity = new TradeEntity(aggregateId.toString());
		entity.setSequenceNumber(Integer.valueOf(baseVersion));
		entity.setAggregateIdentifier(aggregateId.toString());
		entity.setEventIdentifier(randomUUID().toString());
		Event event = newEvents.get(0);
		ObjectMapper map = new ObjectMapper();
		try {
			if(event instanceof TradeReceivedEvent) {
				TradeReceivedEvent e = (TradeReceivedEvent)event;
				entity.setPayload(map.writeValueAsString(e));
			}else {
				TradeCfinRetrievedEvent e = (TradeCfinRetrievedEvent)event;
				entity.setPayload(map.writeValueAsString(e));
			}
			entity.setPayloadType(event.getClass().getTypeName());
			tradeRepository.save(entity);
			mapEvents.put(aggregateId.toString(), newArrayList(entity));
		}catch(JsonProcessingException e) {
			throw new SerializeException(aggregateId);
		}
	}

	@Override
	public List<Event> load(UUID aggregateId) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SerializeException {
		List<TradeEntity> l = mapEvents.get(aggregateId);
		if (CollectionUtils.isEmpty(l)) {
			l = tradeRepository.loadByAggregateId(aggregateId.toString());
		}
		//List<TradeEntity> l = tradeRepository.loadByAggregateId(aggregateId.toString());
		if(CollectionUtils.isNotEmpty(l)) {
			List<Event> eventList = new ArrayList<Event>();
			ObjectMapper map = new ObjectMapper();
			for(TradeEntity te : l) {
				Class<?> clazz = Class.forName(te.getPayloadType());
				Event e;
				try {
					if(clazz.equals(TradeReceivedEvent.class)) {
						e = (TradeReceivedEvent) map.readValue(te.getPayload(), clazz);
					}else {
						e = (TradeCfinRetrievedEvent) map.readValue(te.getPayload(), clazz);
					}
					eventList.add(e);
				}catch(JsonProcessingException ex) {
					throw new SerializeException(aggregateId);
				}
			}
			return eventList;
		}
		return emptyList();
	}

}
