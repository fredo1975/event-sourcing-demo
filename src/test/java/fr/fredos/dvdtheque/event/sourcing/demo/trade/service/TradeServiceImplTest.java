package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSearchCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeSendCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { fr.fredos.dvdtheque.event.sourcing.demo.EventSourcingSpringBootApplication.class })
@ActiveProfiles("jpa")
public class TradeServiceImplTest {
	protected Logger logger = LoggerFactory.getLogger(TradeServiceImplTest.class);
	@Autowired
	TradeService tradeService;

	@Test
	void processReceiveCommandInOneProcessTest() throws TradeNotFoundException, SerializeException,
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		long start = new Date().getTime();
		TradeReceiveCommand command = new TradeReceiveCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d, 50);
		Trade trade = tradeService.processInOneTransaction(command);
		assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getTradeId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		assertNotNull(trade.getCfin());
		assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(2));
		long end = new Date().getTime() - start;
		logger.info("Finished tradeReceiveCommandInOneProcessTest in " + end + " ms");
	}

	@Test
	void processReceiveCommandTest() throws TradeNotFoundException, SerializeException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		long start = new Date().getTime();
		TradeReceiveCommand command = new TradeReceiveCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d, 50);
		Trade trade = tradeService.process(command);
		assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getTradeId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(0));
		long end = new Date().getTime() - start;
		logger.info("Finished tradeReceiveCommandTest in " + end + " ms");
	}
	
	@Test
	void processReceiveCommandAndSearchCfinCommandTest() throws TradeNotFoundException, SerializeException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		long start = new Date().getTime();
		TradeReceiveCommand command = new TradeReceiveCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d, 50);
		Trade trade = tradeService.process(command);
		assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getTradeId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(0));

		TradeSearchCfinCommand tradeSearchCfinCommand = new TradeSearchCfinCommand(trade.getId(), trade.getIsin(),
				trade.getCcy());
		trade = tradeService.process(tradeSearchCfinCommand);
		assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getTradeId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		assertNotNull(trade.getCfin());
		assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(1));
		long end = new Date().getTime() - start;
		logger.info("Finished tradeReceiveCommandTest in " + end + " ms");
	}

	@Test
	void replayAllNotSentEvents() throws ClassNotFoundException {
		long start = new Date().getTime();
		tradeService.replayAllNotSentEvents();
		long end = new Date().getTime() - start;
		logger.info("Finished replayAllNotSentEvents in " + end + " ms ");
	}
	
	@Test
	void loadAllNotSentTradeEntities() throws ClassNotFoundException {
		long start = new Date().getTime();
		List<Event> l = tradeService.loadAllNotSentEvents();
		//assertTrue(CollectionUtils.isNotEmpty(l));
		long end = new Date().getTime() - start;
		logger.info("Finished loadAllNotSentTradeEntities in " + end + " ms and retrieved l.size()=" + l.size());
		/*l.forEach(event -> {
			if(event != null) {
				System.out.println(event.toString());
			}
		});*/
	}

	@Test
	void processReceiveCommandMultiThreadTest() throws TradeNotFoundException, SerializeException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		long start = new Date().getTime();
		for (int i = 0; i < 1000; i++) {
			executor.execute(new MyRunnable(tradeService));
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			// System.out.println("waiting for finish ...");
		}
		long end = new Date().getTime() - start;
		logger.info("Finished all threads in " + end + " ms");
	}

	public class MyRunnable implements Runnable {
		TradeService tradeService;

		MyRunnable(TradeService tradeService) {
			this.tradeService = tradeService;
		}

		@Override
		public void run() {
			TradeReceiveCommand command = new TradeReceiveCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d,
					50);
			Trade trade = null;
			try {
				trade = tradeService.process(command);
			} catch (SerializeException e) {
				e.printStackTrace();
			}
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getTradeId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(0));

			TradeSearchCfinCommand tradeSearchCfinCommand = new TradeSearchCfinCommand(trade.getId(), trade.getIsin(),
					trade.getCcy());
			try {
				trade = tradeService.process(tradeSearchCfinCommand);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getTradeId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertNotNull(trade.getCfin());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(1));
			
			TradeSendCommand tradeSendCommand = new TradeSendCommand(trade.getId());
			try {
				trade = tradeService.process(tradeSendCommand);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getTradeId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertNotNull(trade.getCfin());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(2));
		}
	}

	@Test
	void processReceiveCommandInOneTransactionMultiThreadTest() throws TradeNotFoundException, SerializeException,
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		long start = new Date().getTime();
		for (int i = 0; i < 100; i++) {
			executor.execute(new MyRunnableInOneTransaction(tradeService));
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			// System.out.println("waiting for finish ...");
		}
		long end = new Date().getTime() - start;
		logger.info("Finished all threads in " + end + " ms");
	}

	public class MyRunnableInOneTransaction implements Runnable {
		TradeService tradeService;

		MyRunnableInOneTransaction(TradeService tradeService) {
			this.tradeService = tradeService;
		}

		@Override
		public void run() {
			TradeReceiveCommand command = new TradeReceiveCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d,
					50);
			Trade trade = null;
			try {
				trade = tradeService.processInOneTransaction(command);
			} catch (ClassNotFoundException e) {
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
			assertNotNull(trade.getCfin());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(2));
		}
	}
	
	@Test
	void tradeNotSentInOneProcessMultiThreadTest() throws TradeNotFoundException, SerializeException,
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		long start = new Date().getTime();
		for (int i = 0; i < 5000; i++) {
			executor.execute(new MyRunnableNotSent(tradeService));
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			// System.out.println("waiting for finish ...");
		}
		long end = new Date().getTime() - start;
		logger.info("Finished all threads in " + end + " ms");
	}
	
	public class MyRunnableNotSent implements Runnable {
		TradeService tradeService;

		MyRunnableNotSent(TradeService tradeService) {
			this.tradeService = tradeService;
		}

		@Override
		public void run() {
			TradeReceiveCommand command = new TradeReceiveCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d, 50);
			Trade trade = tradeService.process(command);
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getTradeId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(0));

			TradeSearchCfinCommand tradeSearchCfinCommand = new TradeSearchCfinCommand(trade.getId(), trade.getIsin(),
					trade.getCcy());
			try {
				trade = tradeService.process(tradeSearchCfinCommand);
			} catch (ClassNotFoundException e) {
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
			assertNotNull(trade.getCfin());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(1));
		}
	}
}
