package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import static java.util.UUID.randomUUID;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSearchCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSendCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.EventStore;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;

@Service
public class TradeServiceImpl implements TradeService {
	@Autowired
	private EventStore jpaEventStore;
	@Autowired
    private Retrier conflictRetrier;
	
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW)
    public Trade processInOneTransaction(TradeReceiveCommand command) throws ClassNotFoundException {
        Trade trade = new Trade(randomUUID().toString(), command.getTradeId(), command.getIsin(), command.getCcy(),command.getPrice(),command.getQuantity());
        storeEvents(trade);
        TradeSearchCfinCommand tradeSearchCfinCommand = new TradeSearchCfinCommand(trade.getId(),trade.getIsin(),trade.getCcy());
        trade = processInOneTransaction(tradeSearchCfinCommand);
        TradeSendCommand tradeSendCommand = new TradeSendCommand(trade.getId());
        trade = processInOneTransaction(tradeSendCommand);
        return trade;
    }
	
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW)
    public Trade process(TradeReceiveCommand command) throws SerializeException {
        Trade trade = new Trade(randomUUID().toString(), command.getTradeId(), command.getIsin(), command.getCcy(),command.getPrice(),command.getQuantity());
        storeEvents(trade);
        return trade;
    }

    public Optional<Trade> loadTrade(String id) throws ClassNotFoundException{
        List<Event> eventStream = jpaEventStore.load(id);
        if (eventStream.isEmpty()) return Optional.empty();
        return Optional.of(new Trade(id, eventStream));
    }
    @Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW)
    public Trade process(TradeSearchCfinCommand command) throws ClassNotFoundException {
    	return process(command.getId(), trade -> trade.searchCfin(command.getId(), command.getIsin(), command.getCcy()));
    }
    @Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW)
    public Trade process(TradeSendCommand command) throws ClassNotFoundException {
    	return process(command.getId(), trade -> trade.send(command.getId()));
    }
    public Trade processInOneTransaction(TradeSendCommand command) throws ClassNotFoundException {
    	return process(command.getId(), trade -> trade.send(command.getId()));
    }
    
    public Trade processInOneTransaction(TradeSearchCfinCommand command) throws ClassNotFoundException {
    	return process(command.getId(), trade -> trade.searchCfin(command.getId(), command.getIsin(), command.getCcy()));
    }

    private Trade process(String id, Consumer<Trade> consumer) throws ClassNotFoundException {

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

    private void storeEvents(Trade trade){
    	jpaEventStore.store(trade.getId(), trade.getNewEvents(), trade.getBaseVersion());
    }
    
    public List<Event> loadAllNotSentEvents() throws ClassNotFoundException{
    	return jpaEventStore.loadAllNotSentEvents();
    }
}
