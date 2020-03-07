package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import java.util.List;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveBookCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveCancelCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSearchCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSearchFailCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSendCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;

public interface TradeService {

	Trade process(TradeReceiveBookCommand command);
	Trade processCancelInOneTransaction(TradeReceiveCancelCommand command);
	Trade process(TradeSearchCfinCommand command);
	Trade process(TradeSendCommand command);
	Trade processInOneTransaction(TradeSearchFailCfinCommand command);
	Trade processInOneTransaction(TradeReceiveBookCommand command);
	Trade processInOneTransaction(TradeSearchCfinCommand command);
	Trade processInOneTransaction(TradeSendCommand command);
	List<Event> loadAllNotSentEvents();
	void replayAllNotSentEvents();
	List<Event> loadAllEvents();
}
