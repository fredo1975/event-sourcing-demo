package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import static java.util.UUID.randomUUID;

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

	private Trade jumpToNextCommand(Trade trade) throws ClassNotFoundException {
		Event event = trade.getNewEvents().get(0);
		Class<? extends TradeCommand> nextCommand = map.get(event.getClass());
		if(nextCommand.equals(TradeSendCommand.class)) {
			TradeSendCommand tradeSendCommand = new TradeSendCommand(trade.getId());
			return processInOneTransaction(tradeSendCommand);
		}else if (nextCommand.equals(TradeSearchCfinCommand.class)) {
			TradeSearchCfinCommand tradeSearchCfinCommand = new TradeSearchCfinCommand(trade.getId(), trade.getIsin(),
					trade.getCcy());
			trade = processInOneTransaction(tradeSearchCfinCommand);
		} else if (nextCommand.equals(TradeSendCommand.class)) {
			TradeSendCommand tradeSendCommand = new TradeSendCommand(trade.getId());
			trade = processInOneTransaction(tradeSendCommand);
		}else {
			throw new NextCommandNotFoundException(trade.getId(),nextCommand);
		}
		return jumpToNextCommand(trade);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	public Trade processInOneTransaction(TradeReceiveCommand command) throws ClassNotFoundException {
		Trade trade = new Trade(randomUUID().toString(), command.getTradeId(), command.getIsin(), command.getCcy(),
				command.getPrice(), command.getQuantity());
		storeEvents(trade);
		return jumpToNextCommand(trade);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Override
	public Trade process(TradeReceiveCommand command) throws SerializeException {
		Trade trade = new Trade(randomUUID().toString(), command.getTradeId(), command.getIsin(), command.getCcy(),
				command.getPrice(), command.getQuantity());
		logger.debug("processing TradeReceiveCommand with id=" + trade.getId());
		storeEvents(trade);
		return trade;
	}

	public Optional<Trade> loadTrade(String id) throws ClassNotFoundException {
		List<Event> eventStream = jpaEventStore.load(id);
		if (eventStream.isEmpty())
			return Optional.empty();
		return Optional.of(new Trade(id, eventStream));
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

	@Override
	public Trade processInOneTransaction(TradeSendCommand command) throws ClassNotFoundException {
		logger.debug("processing TradeSendCommand with id=" + command.getId());
		return process(command.getId(), trade -> trade.send(command.getId()));
	}

	@Override
	public Trade processInOneTransaction(TradeSearchCfinCommand command) throws ClassNotFoundException {
		logger.debug("processing TradeSearchCfinCommand with id=" + command.getId());
		return process(command.getId(),
				trade -> trade.searchCfin(command.getId(), command.getIsin(), command.getCcy()));
	}

	private Trade process(String id, Consumer<Trade> consumer) throws ClassNotFoundException {

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
	public void replayAllNotSentEvents() throws ClassNotFoundException {
		List<Event> eventsList = loadAllNotSentEvents();
		if (CollectionUtils.isNotEmpty(eventsList)) {
			eventsList.forEach(event -> {
				if (event instanceof TradeReceivedEvent) {
					TradeReceivedEvent e = (TradeReceivedEvent) event;
					TradeSearchCfinCommand tradeSearchCfinCommand = new TradeSearchCfinCommand(event.getAggregateId(),
							((TradeReceivedEvent) event).getIsin(), ((TradeReceivedEvent) event).getCcy());
					Trade trade = null;
					try {
						trade = process(tradeSearchCfinCommand);
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
					TradeSendCommand tradeSendCommand = new TradeSendCommand(event.getAggregateId());
					try {
						process(tradeSendCommand);
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				} else if (event instanceof TradeCfinRetrievedEvent) {
					TradeCfinRetrievedEvent e = (TradeCfinRetrievedEvent) event;
					TradeSendCommand command = new TradeSendCommand(event.getAggregateId());
					try {
						process(command);
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public List<Event> loadAllNotSentEvents() throws ClassNotFoundException {
		return jpaEventStore.loadAllNotSentEvents();
	}
}
