package fr.fredos.dvdtheque.event.sourcing.demo.events.store;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeSentEvent;
import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeEntity;
import fr.fredos.dvdtheque.event.sourcing.demo.trade.service.DeserializeException;
import fr.fredos.dvdtheque.event.sourcing.demo.trade.service.SerializeException;
import fr.fredos.dvdtheque.event.sourcing.demo.trade.service.UnknownEventException;
@Component("jpaEventStore")
public class JpaEventStore implements EventStore{
	protected Logger logger = LoggerFactory.getLogger(JpaEventStore.class);
	@Autowired
	TradeRepository tradeRepository;
	private IMap<String, List<TradeEntity>> mapTradeEntity;
	@Autowired
	private HazelcastInstance instance;
	@PostConstruct
	public void init() {
		mapTradeEntity = instance.getMap("events");
	}
	
	@Override
	public void store(String aggregateId, List<Event> newEvents, int baseVersion){
		TradeEntity entity = new TradeEntity(aggregateId);
		entity.setSequenceNumber(Integer.valueOf(baseVersion));
		entity.setAggregateIdentifier(aggregateId);
		entity.setEventIdentifier(randomUUID().toString());
		entity.setTimeStamp(ZonedDateTime.now());
		Event event = newEvents.get(0);
		ObjectMapper map = new ObjectMapper();
		try {
			if(event instanceof TradeReceivedEvent) {
				TradeReceivedEvent e = (TradeReceivedEvent)event;
				entity.setPayload(map.writeValueAsString(e));
			}else if(event instanceof TradeSentEvent) {
				TradeSentEvent e = (TradeSentEvent)event;
				entity.setPayload(map.writeValueAsString(e));
			}else if(event instanceof TradeCfinRetrievedEvent){
				TradeCfinRetrievedEvent e = (TradeCfinRetrievedEvent)event;
				entity.setPayload(map.writeValueAsString(e));
			}
			entity.setPayloadType(event.getClass().getTypeName());
			List<TradeEntity> l = mapTradeEntity.get(aggregateId.toString());
			if (CollectionUtils.isEmpty(l)) {
				mapTradeEntity.put(aggregateId.toString(), newArrayList(entity));
			}else {
				l.add(entity);
				mapTradeEntity.put(aggregateId.toString(), l);
			}
			tradeRepository.save(entity);
		}catch(JsonProcessingException e) {
			throw new SerializeException(aggregateId.toString(),event.getClass().getTypeName());
		}
	}

	@Override
	public List<Event> load(String aggregateId){
		List<TradeEntity> tradeEntityList = mapTradeEntity.get(aggregateId);
		if (CollectionUtils.isEmpty(tradeEntityList)) {
			tradeEntityList = tradeRepository.loadByAggregateId(aggregateId.toString());
		}
		return transformTradeEntityListToEventList(tradeEntityList);
	}
	
	private List<Event> transformTradeEntityListToEventList(List<TradeEntity> tradeEntityList) {
		if(CollectionUtils.isNotEmpty(tradeEntityList)) {
			List<Event> eventList = new ArrayList<Event>();
			ObjectMapper map = new ObjectMapper();
			for(TradeEntity te : tradeEntityList) {
				Event e=null;
				try {
					Class<?> clazz = Class.forName(te.getPayloadType());
					if(clazz.equals(TradeReceivedEvent.class)) {
						e = (TradeReceivedEvent) map.readValue(te.getPayload(), clazz);
					}else if(clazz.equals(TradeSentEvent.class)) {
						e = (TradeSentEvent) map.readValue(te.getPayload(), clazz);
					}else if(clazz.equals(TradeCfinRetrievedEvent.class)){
						e = (TradeCfinRetrievedEvent) map.readValue(te.getPayload(), clazz);
					}else {
						new UnknownEventException(te.getAggregateIdentifier(),te.getPayloadType());
					}
					eventList.add(e);
				}catch(JsonProcessingException ex) {
					throw new DeserializeException(te.getAggregateIdentifier(),te.getPayloadType());
				}catch(ClassNotFoundException ce) {
					logger.error(ce.getException().getMessage());
				}
			}
			return eventList;
		}
		return emptyList();
	}
	@Override
	public List<Event> loadAllNotSentEvents(){
		return transformTradeEntityListToEventList(tradeRepository.loadAllNotSentEvents());
	}
	@Override
	public List<Event> loadAllEvents() {
		return transformTradeEntityListToEventList(tradeRepository.loadAllEvents());
	}
}
