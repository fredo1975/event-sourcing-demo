package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeCrud;
import fr.fredos.dvdtheque.event.sourcing.demo.events.store.TradeCrudRepository;
import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeCrudEntity;
@Service
public class TradeCrudServiceImpl implements TradeCrudService {
	@Autowired
	TradeCrudRepository tradeCrudRepository;
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW)
	@Override
	public TradeCrudEntity save(TradeCrud trade) {
		TradeCrudEntity tradeCrudEntity = new TradeCrudEntity();
		tradeCrudEntity.setCcy(trade.getCcy());
		tradeCrudEntity.setIsin(trade.getIsin());
		tradeCrudEntity.setPrice(trade.getPrice());
		tradeCrudEntity.setQuantity(trade.getQuantity());
		return tradeCrudRepository.save(tradeCrudEntity);
	}
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW)
	public TradeCrudEntity update(TradeCrudEntity trade) {
		Optional<TradeCrudEntity> optionalTradeCrudEntity = tradeCrudRepository.findById(trade.getId());
		if(optionalTradeCrudEntity.isPresent()) {
			TradeCrudEntity tradeCrudEntity = optionalTradeCrudEntity.get();
			tradeCrudEntity.setCfin(trade.getCfin());
			return tradeCrudEntity;
		}else {
			throw new TradeNotFoundException(trade.getId());
		}
	}
}
