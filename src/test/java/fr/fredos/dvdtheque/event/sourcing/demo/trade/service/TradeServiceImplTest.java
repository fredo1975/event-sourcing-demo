package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;


import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.event.sourcing.demo.EventSourcingSpringBootApplication.class})
//@ActiveProfiles("local")
public class TradeServiceImplTest {

	@Autowired
	TradeService tradeService;
	@Test
	public void tradeReceiveCommandTest() {
		TradeReceiveCommand command = new TradeReceiveCommand(randomUUID(), "FR0000", "EUR", 1000.0d, 50);
		Trade trade = tradeService.process(command);
		assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getTradeId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		assertNotNull(trade.getCfin());
		assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(1));
	}
}
