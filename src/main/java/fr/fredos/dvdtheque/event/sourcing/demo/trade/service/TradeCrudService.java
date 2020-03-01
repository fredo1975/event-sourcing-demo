package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeCrud;
import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeCrudEntity;

public interface TradeCrudService {

	TradeCrudEntity save(TradeCrud trade);
	TradeCrudEntity update(TradeCrudEntity trade);
	TradeCrudEntity processInOneTransaction(TradeCrud trade);
}
