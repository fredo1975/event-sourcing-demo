package fr.fredos.dvdtheque.event.sourcing.demo.events.store;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;

import java.time.LocalDateTime;
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
import fr.fredos.dvdtheque.event.sourcing.demo.mybatis.dao.TradeDao;
import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeDbObject;
import fr.fredos.dvdtheque.event.sourcing.demo.trade.service.DeserializeException;
import fr.fredos.dvdtheque.event.sourcing.demo.trade.service.SerializeException;
@Component("jpaEventStore")
public class DbEventStore implements EventStore{
	protected Logger logger = LoggerFactory.getLogger(DbEventStore.class);
	@Autowired
	TradeDao tradeDao;
	
	private IMap<String, List<TradeDbObject>> mapTradeEntity;
	@Autowired
	private HazelcastInstance instance;
	@PostConstruct
	public void init() {
		mapTradeEntity = instance.getMap("events");
	}
	
	@Override
	public void store(String aggregateId, List<Event> newEvents, int baseVersion){
		TradeDbObject entity = new TradeDbObject(aggregateId);
		entity.setSequenceNumber(Integer.valueOf(baseVersion));
		entity.setAggregateIdentifier(aggregateId);
		entity.setEventIdentifier(randomUUID().toString());
		entity.setTimestamp(LocalDateTime.now());
		Event event = newEvents.get(0);
		ObjectMapper map = new ObjectMapper();
		try {
			entity.setPayload(map.writeValueAsString(event));
			entity.setPayloadType(event.getClass().getTypeName());
			List<TradeDbObject> l = mapTradeEntity.get(aggregateId.toString());
			if (CollectionUtils.isEmpty(l)) {
				mapTradeEntity.put(aggregateId.toString(), newArrayList(entity));
			}else {
				l.add(entity);
				mapTradeEntity.put(aggregateId.toString(), l);
			}
			tradeDao.save(entity);
		}catch(JsonProcessingException e) {
			throw new SerializeException(aggregateId.toString(),event.getClass().getTypeName());
		}
	}

	@Override
	public List<Event> load(String aggregateId){
		List<TradeDbObject> tradeEntityList = mapTradeEntity.get(aggregateId);
		if (CollectionUtils.isEmpty(tradeEntityList)) {
			tradeEntityList = tradeDao.loadByAggregateId(aggregateId.toString());
		}
		return transformTradeEntityListToEventList(tradeEntityList);
	}
	
	private List<Event> transformTradeEntityListToEventList(List<TradeDbObject> tradeEntityList) {
		if(CollectionUtils.isNotEmpty(tradeEntityList)) {
			List<Event> eventList = new ArrayList<Event>();
			ObjectMapper map = new ObjectMapper();
			for(TradeDbObject te : tradeEntityList) {
				try {
					Class<?> clazz = Class.forName(te.getPayloadType());
					eventList.add((Event) map.readValue(te.getPayload(), clazz));
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
		return transformTradeEntityListToEventList(tradeDao.loadAllNotSentEvents());
	}
	@Override
	public List<Event> loadAllEvents() {
		return transformTradeEntityListToEventList(tradeDao.loadAllEvents());
	}
}
