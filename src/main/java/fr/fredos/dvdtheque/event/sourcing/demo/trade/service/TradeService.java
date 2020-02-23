package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSearchCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;

public interface TradeService {

	Trade process(TradeReceiveCommand command);
}
