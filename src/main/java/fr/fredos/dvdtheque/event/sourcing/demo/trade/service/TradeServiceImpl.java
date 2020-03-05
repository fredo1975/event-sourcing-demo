package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSearchCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSearchFailCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSendCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.EventStore;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeCfinRetrievedEvent;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeReceivedEvent;

@Service
public class TradeServiceImpl implements TradeService {
	protected Logger logger = LoggerFactory.getLogger(TradeServiceImpl.class);
	@Autowired
	private EventStore jpaEventStore;
	@Autowired
	private Retrier conflictRetrier;

	private Map<Class<? extends Event>, Class<? extends TradeCommand>> map;

	@PostConstruct
	public void init() {
		map = new HashMap<>();
		map.put(TradeCfinRetrievedEvent.class, TradeSendCommand.class);
		map.put(TradeReceivedEvent.class, TradeSearchCfinCommand.class);
	}

	private Trade jumpToNextCommand(Trade trade){
		Event event = trade.getNewEvents().get(0);
		Class<? extends TradeCommand> nextCommand = map.get(event.getClass());
		if(nextCommand.equals(TradeSendCommand.class)) {
			return processInOneTransaction(new TradeSendCommand(trade.getId()));
		}else if (nextCommand.equals(TradeSearchCfinCommand.class)) {
			try {
				trade = processInOneTransaction(new TradeSearchCfinCommand(trade.getId(), trade.getIsin(), trade.getCcy()));
			}catch(RuntimeException e) {
				return processInOneTransaction(new TradeSearchFailCfinCommand(trade.getId(), e.getMessage()));
			}
			
		}else {
			throw new NextCommandNotFoundException(trade.getId(),nextCommand);
		}
		return jumpToNextCommand(trade);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	public Trade processInOneTransaction(TradeReceiveCommand command) throws ClassNotFoundException {
		Trade trade = new Trade(command.getTradeId(), command.getIsin(), command.getCcy(),command.getPrice(),command.getQuantity());
		storeEvents(trade);
		return jumpToNextCommand(trade);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	public Trade process(TradeReceiveCommand command) throws SerializeException {
		Trade trade = new Trade(command.getTradeId(), command.getIsin(), command.getCcy(),
				command.getPrice(), command.getQuantity());
		logger.debug("processing TradeReceiveCommand with id=" + trade.getId());
		storeEvents(trade);
		return trade;
	}

	public Optional<Trade> loadTrade(String id){
		List<Event> eventStream = jpaEventStore.load(id);
		if (eventStream.isEmpty())
			return Optional.empty();
		return Optional.of(new Trade(id, eventStream));
	}

	@Override
	public Trade processInOneTransaction(TradeSendCommand command) {
		logger.debug("processing TradeSendCommand with id=" + command.getId());
		return process(command.getId(), trade -> trade.send(command.getId()));
	}
	@Override
	public Trade processInOneTransaction(TradeSearchCfinCommand command) {
		logger.debug("processing TradeSearchCfinCommand with id=" + command.getId());
		throw new UnknownEventException(command.getId(),"TradeCfinRetrievedEvent");
		/*
		return process(command.getId(),
				trade -> trade.searchCfin(command.getId(), command.getIsin(), command.getCcy()));*/
	}
	
	@Override
	public Trade processInOneTransaction(TradeSearchFailCfinCommand command) {
		logger.debug("processing TradeSearchFailCfinCommand with id=" + command.getId());
		return process(command.getId(),
				trade -> trade.searchFailCfin(command.getErrorMessage()));
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	public Trade process(TradeSearchCfinCommand command) throws ClassNotFoundException {
		logger.debug("processing TradeSearchCfinCommand with id=" + command.getId());
		return process(command.getId(),
				trade -> trade.searchCfin(command.getId(), command.getIsin(), command.getCcy()));
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	public Trade process(TradeSendCommand command) throws ClassNotFoundException {
		logger.debug("processing TradeSendCommand with id=" + command.getId());
		return process(command.getId(), trade -> trade.send(command.getId()));
	}

	private Trade process(String id, Consumer<Trade> consumer) {

		/*
		 * return conflictRetrier.get(() -> { Optional<Trade> possibleTrade =
		 * loadTrade(tradeId); Trade trade = possibleTrade.orElseThrow(() -> new
		 * TradeNotFoundException(tradeId)); consumer.accept(trade); storeEvents(trade);
		 * return trade; });
		 */

		Optional<Trade> possibleTrade = loadTrade(id);
		Trade trade = possibleTrade.orElseThrow(() -> new TradeNotFoundException(id));
		consumer.accept(trade);
		storeEvents(trade);
		return trade;
	}

	private void storeEvents(Trade trade) {
		jpaEventStore.store(trade.getId(), trade.getNewEvents(), trade.getBaseVersion());
	}

	@Override
	public void replayAllNotSentEvents(){
		List<Event> eventsList = loadAllNotSentEvents();
		if (CollectionUtils.isNotEmpty(eventsList)) {
			eventsList.forEach(event -> {
				if (event instanceof TradeReceivedEvent) {
					Trade trade = null;
					try {
						trade = process(new TradeSearchCfinCommand(event.getAggregateId(),
								((TradeReceivedEvent) event).getIsin(), ((TradeReceivedEvent) event).getCcy()));
						jumpToNextCommand(trade);
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				} else if (event instanceof TradeCfinRetrievedEvent) {
					try {
						process(new TradeSendCommand(event.getAggregateId()));
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public List<Event> loadAllNotSentEvents() {
		return jpaEventStore.loadAllNotSentEvents();
	}
	
	@Override
	public List<Event> loadAllEvents() {
		return jpaEventStore.loadAllEvents();
	}
}
