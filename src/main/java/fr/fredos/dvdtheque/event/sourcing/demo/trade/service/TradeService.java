package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import java.util.List;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveBookCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveCancelCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSearchCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSendCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;

public interface TradeService {

	Trade process(TradeReceiveBookCommand command);
	Trade processCancelInOneTransaction(TradeReceiveCancelCommand command);
	Trade process(TradeSearchCfinCommand command) throws ClassNotFoundException;
	Trade process(TradeSendCommand command) throws ClassNotFoundException;
	Trade processInOneTransaction(TradeReceiveBookCommand command) throws ClassNotFoundException;
	Trade processInOneTransaction(TradeSearchCfinCommand command) throws ClassNotFoundException;
	Trade processInOneTransaction(TradeSendCommand command) throws ClassNotFoundException;
	List<Event> loadAllNotSentEvents();
	void replayAllNotSentEvents();
	List<Event> loadAllEvents();
}
