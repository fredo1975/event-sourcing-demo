package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import static java.util.UUID.randomUUID;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSearchCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.EventStore;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.OptimisticLockingException;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;

@Service
public class TradeServiceImpl implements TradeService {
	@Autowired
	private EventStore jpaEventStore;
	@Autowired
    private Retrier conflictRetrier;

    public Trade process(TradeReceiveCommand command) {
        Trade trade = new Trade(randomUUID(), command.getTradeId(), command.getIsin(), command.getCcy(),command.getPrice(),command.getQuantity());
        storeEvents(trade);
        TradeSearchCfinCommand tradeSearchCfinCommand = new TradeSearchCfinCommand(trade.getId(),trade.getIsin(),trade.getCcy());
        trade = process(tradeSearchCfinCommand);
        return trade;
    }

    public Optional<Trade> loadTrade(UUID id) {
        List<Event> eventStream = jpaEventStore.load(id);
        if (eventStream.isEmpty()) return Optional.empty();
        return Optional.of(new Trade(id, eventStream));
    }

    private Trade process(TradeSearchCfinCommand command) {
    	return process(command.getId(), trade -> trade.searchCfin(command.getId(), command.getIsin(), command.getCcy()));
    }

    private Trade process(UUID id, Consumer<Trade> consumer)
            throws TradeNotFoundException, OptimisticLockingException {

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

    private void storeEvents(Trade trade) {
    	jpaEventStore.store(trade.getId(), trade.getNewEvents(), trade.getBaseVersion());
    }
}
