package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;


import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSearchCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.OptimisticLockingException;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.event.sourcing.demo.EventSourcingSpringBootApplication.class})
@ActiveProfiles("jpa")
public class TradeServiceImplTest {

	@Autowired
	TradeService tradeService;
	
	@Test
	void tradeReceiveCommandTest() throws TradeNotFoundException, OptimisticLockingException, ClassNotFoundException, InstantiationException, IllegalAccessException, JsonProcessingException {
		
		TradeReceiveCommand command = new TradeReceiveCommand(randomUUID(), "FR0000", "EUR", 1000.0d, 50);
		Trade trade = tradeService.process(command);
		assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getTradeId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		//assertNotNull(trade.getCfin());
		assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(0));
		
		TradeSearchCfinCommand tradeSearchCfinCommand = new TradeSearchCfinCommand(trade.getId(),trade.getIsin(),trade.getCcy());
        trade = tradeService.process(tradeSearchCfinCommand);
        assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getTradeId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		//assertNotNull(trade.getCfin());
		assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(1));
	}
	
	@Test
	void tradeReceiveCommandMultiThreadTest() throws TradeNotFoundException, OptimisticLockingException, ClassNotFoundException, InstantiationException, IllegalAccessException, JsonProcessingException {
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		long start = new Date().getTime();
		for(int i=0;i<500;i++) {
			executor.execute(new MyRunnable(tradeService));
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			//System.out.println("waiting for finish ...");
		}
		long end = new Date().getTime()-start;
		System.out.println("Finished all threads in "+end + " ms");
	}
	
	public class MyRunnable implements Runnable {
		TradeService tradeService;
		MyRunnable(TradeService tradeService) {
			this.tradeService = tradeService;
		}
		@Override
		public void run() {
			TradeReceiveCommand command = new TradeReceiveCommand(randomUUID(), "FR0000", "EUR", 1000.0d, 50);
			Trade trade=null;
			try {
				trade = tradeService.process(command);
			} catch (OptimisticLockingException | JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getTradeId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			//assertNotNull(trade.getCfin());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(0));
			
			TradeSearchCfinCommand tradeSearchCfinCommand = new TradeSearchCfinCommand(trade.getId(),trade.getIsin(),trade.getCcy());
	        try {
				trade = tradeService.process(tradeSearchCfinCommand);
			} catch (TradeNotFoundException | OptimisticLockingException | ClassNotFoundException
					| InstantiationException | IllegalAccessException | JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getTradeId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			//assertNotNull(trade.getCfin());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(1));
		}
	}
}
