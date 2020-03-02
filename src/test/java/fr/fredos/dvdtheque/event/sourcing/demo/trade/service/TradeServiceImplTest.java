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
	void processReceiveCommandInOneProcessTest() throws SerializeException,
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		long start = new Date().getTime();
		Trade trade = tradeService.processInOneTransaction(new TradeReceiveCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d, 50));
		assertNotNull(trade);
		assertNotNull(trade.getId());
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
	void processReceiveCommandTest() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		long start = new Date().getTime();
		Trade trade = tradeService.process(new TradeReceiveCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d, 50));
		assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(0));
		long end = new Date().getTime() - start;
		logger.info("Finished tradeReceiveCommandTest in " + end + " ms");
	}
	
	@Test
	void processReceiveCommandAndSearchCfinCommandTest() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		long start = new Date().getTime();
		Trade trade = tradeService.process(new TradeReceiveCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d, 50));
		assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(0));

		trade = tradeService.process( new TradeSearchCfinCommand(trade.getId(), trade.getIsin(),
				trade.getCcy()));
		assertNotNull(trade);
		assertNotNull(trade.getId());
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
	void chainProcess() {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		long start = new Date().getTime();
		for (int i = 0; i < 150; i++) {
			executor.execute(new MyRunnableNotSent(tradeService));
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			// System.out.println("waiting for finish ...");
		}
		long end = new Date().getTime() - start;
		logger.info("Finished all threads in " + end + " ms");
		
		
		long start2 = new Date().getTime();
		List<Event> l = tradeService.loadAllNotSentEvents();
		//assertTrue(CollectionUtils.isNotEmpty(l));
		long end2 = new Date().getTime() - start2;
		logger.info("Finished loadAllNotSentTradeEntities in " + end2 + " ms and retrieved l.size()=" + l.size());
		
		long start3 = new Date().getTime();
		tradeService.replayAllNotSentEvents();
		long end3 = new Date().getTime() - start3;
		logger.info("Finished replayAllNotSentEvents in " + end3 + " ms ");
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
	void processReceiveCommandMultiThreadTest() throws ClassNotFoundException,
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
			Trade trade = null;
			try {
				trade = tradeService.process(new TradeReceiveCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d,
						50));
			} catch (SerializeException e) {
				e.printStackTrace();
			}
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(0));

			try {
				trade = tradeService.process(new TradeSearchCfinCommand(trade.getId(), trade.getIsin(),
						trade.getCcy()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertNotNull(trade.getCfin());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(1));
			
			try {
				trade = tradeService.process(new TradeSendCommand(trade.getId()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertNotNull(trade.getCfin());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(2));
		}
	}

	@Test
	void processReceiveCommandInOneTransactionMultiThreadTest() throws ClassNotFoundException {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		long start = new Date().getTime();
		for (int i = 0; i < 1000; i++) {
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
			Trade trade = null;
			try {
				trade = tradeService.processInOneTransaction(new TradeReceiveCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d,
						50));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertNotNull(trade.getCfin());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(2));
		}
	}
	
	@Test
	void processNotSentInOneProcessMultiThreadTest() throws ClassNotFoundException {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		long start = new Date().getTime();
		for (int i = 0; i < 1200; i++) {
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
			Trade trade = tradeService.process(new TradeReceiveCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d, 50));
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(0));
/*
			try {
				trade = tradeService.process(new TradeSearchCfinCommand(trade.getId(), trade.getIsin(),
						trade.getCcy()));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertNotNull(trade.getCfin());
			assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(1));*/
		}
	}
}
