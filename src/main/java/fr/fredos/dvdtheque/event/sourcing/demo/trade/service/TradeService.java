package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import java.util.List;
import java.util.Optional;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeEnterManualCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveBookCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveCancelCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSearchCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSendCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;

public interface TradeService {
	Trade processCancel(TradeReceiveCancelCommand command);
	Trade process(TradeReceiveBookCommand command);
	Trade process(TradeSearchCfinCommand command);
	Trade process(TradeSendCommand command);
	Trade process(TradeEnterManualCfinCommand command);
	List<Event> loadAllNotSentEvents();
	void replayAllNotSentEvents();
	List<Event> loadAllEvents();
	Optional<Trade> loadTrade(String id);
	
	// for test
	Trade processCfinFailed(TradeSearchCfinCommand command);
	Trade processCfinFailed(TradeReceiveBookCommand command);
}
