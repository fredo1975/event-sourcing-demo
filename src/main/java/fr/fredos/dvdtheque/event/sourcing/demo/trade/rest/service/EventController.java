package fr.fredos.dvdtheque.event.sourcing.demo.trade.rest.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeEnterManualCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;
import fr.fredos.dvdtheque.event.sourcing.demo.trade.service.TradeService;

@RestController
@ComponentScan({"fr.fredos.dvdtheque.event.sourcing.demo.trade"})
@RequestMapping("/events")
public class EventController {
	protected Logger logger = LoggerFactory.getLogger(EventController.class);
	@Autowired
	private TradeService tradeService;
	
	@GetMapping("/load/byAggregateId/{aggregateId}")
	ResponseEntity<Trade> loadTrade(@RequestParam(name="aggregateId",required = true) String aggregateId) {
		Optional<Trade> possibleTrade = tradeService.loadTrade(aggregateId);
		if(possibleTrade.isPresent()) {
			Trade trade = possibleTrade.get();
			return ResponseEntity.ok(trade);
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping("/manualCfin/byAggregateId/{aggregateId}")
	ResponseEntity<Trade> processTradeManualCfin(@RequestParam(name="cfin",required = true) String cfin,
			@PathVariable String aggregateId) {
		Optional<Trade> possibleTrade = tradeService.loadTrade(aggregateId);
		if(possibleTrade.isPresent()) {
			Trade trade = tradeService.process(new TradeEnterManualCfinCommand(aggregateId,cfin));
			if(trade == null) {
				return ResponseEntity.badRequest().build();
			}
			return ResponseEntity.ok(trade);
		}else {
			return ResponseEntity.notFound().build();
		}
	}
}
