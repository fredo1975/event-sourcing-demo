package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import static java.util.UUID.randomUUID;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSearchCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.EventStore;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;
import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeEntity;

@Service
public class TradeServiceImpl implements TradeService {
	@Autowired
	private EventStore jpaEventStore;
	@Autowired
    private Retrier conflictRetrier;
	
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW)
    public Trade process(TradeReceiveCommand command) throws SerializeException {
        Trade trade = new Trade(randomUUID(), command.getTradeId(), command.getIsin(), command.getCcy(),command.getPrice(),command.getQuantity());
        storeEvents(trade);
        return trade;
    }

    public Optional<Trade> loadTrade(UUID id) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SerializeException {
        List<Event> eventStream = jpaEventStore.load(id);
        if (eventStream.isEmpty()) return Optional.empty();
        return Optional.of(new Trade(id, eventStream));
    }
    @Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW)
    public Trade process(TradeSearchCfinCommand command) throws TradeNotFoundException, SerializeException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    	return process(command.getId(), trade -> trade.searchCfin(command.getId(), command.getIsin(), command.getCcy()));
    }

    private Trade process(UUID id, Consumer<Trade> consumer)
            throws TradeNotFoundException, SerializeException, ClassNotFoundException, InstantiationException, IllegalAccessException {

    	/*
        return conflictRetrier.get(() -> {
            Optional<Trade> possibleTrade = loadTrade(tradeId);
            Trade trade = possibleTrade.orElseThrow(() -> new TradeNotFoundException(tradeId));
            consumer.accept(trade);
            storeEvents(trade);
            return trade;
        });*/
    	
    	Optional<Trade> possibleTrade = loadTrade(id);
        Trade trade = possibleTrade.orElseThrow(() -> new TradeNotFoundException(id));
        consumer.accept(trade);
        storeEvents(trade);
        return trade;
    }

    private void storeEvents(Trade trade) throws SerializeException {
    	jpaEventStore.store(trade.getId(), trade.getNewEvents(), trade.getBaseVersion());
    }
}
