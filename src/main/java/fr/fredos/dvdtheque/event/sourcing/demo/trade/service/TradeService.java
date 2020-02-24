package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSearchCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.OptimisticLockingException;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;

public interface TradeService {

	Trade process(TradeReceiveCommand command) throws OptimisticLockingException, JsonProcessingException;
	Trade process(TradeSearchCfinCommand command) throws TradeNotFoundException, OptimisticLockingException, ClassNotFoundException, InstantiationException, IllegalAccessException, JsonProcessingException;
}
