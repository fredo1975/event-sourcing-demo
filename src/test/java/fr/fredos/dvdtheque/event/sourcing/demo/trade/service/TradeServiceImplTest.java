package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeEnterManualCfinCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveBookCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveCancelCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.Trade;

@SpringBootTest(classes = { fr.fredos.dvdtheque.event.sourcing.demo.EventSourcingSpringBootApplication.class })
@ActiveProfiles("mybatis")
public class TradeServiceImplTest {
	protected Logger logger = LoggerFactory.getLogger(TradeServiceImplTest.class);
	@Autowired
	private TradeService tradeService;

	@Test
	void processReceiveBookCommandTest() throws SerializeException,
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		long start = new Date().getTime();
		Trade trade = tradeService.process(new TradeReceiveBookCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d, 50));
		assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		assertEquals("00000", trade.getCfin());
		assertEquals(Integer.valueOf(2),Integer.valueOf(trade.getBaseVersion()));
		long end = new Date().getTime() - start;
		logger.info("Finished processReceiveBookCommandTest in " + end + " ms");
	}
	
	@Test
	void processReceiveBookCommandAndFailCfinTest() throws SerializeException,
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		final String id = randomUUID().toString();
		long start = new Date().getTime();
		tradeService.processCfinFailed(new TradeReceiveBookCommand(id, "FR0000", "EUR", 1000.0d, 50));
		Trade trade = tradeService.process(new TradeEnterManualCfinCommand(id,"00000"));
		assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		assertEquals("00000", trade.getCfin());
		assertEquals(Integer.valueOf(3),Integer.valueOf(trade.getBaseVersion()));
		long end = new Date().getTime() - start;
		logger.info("Finished id="+id+" processReceiveBookCommandTest in " + end + " ms");
	}
	
	@Test
	void processOneTransactionByReceiveAndCancelCommandInOneProcessTest() throws SerializeException,
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		final String id = randomUUID().toString();
		long start = new Date().getTime();
		Trade trade = tradeService.process(new TradeReceiveBookCommand(id, "FR0000", "EUR", 1000.0d, 50));
		assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		assertNotNull(trade.getCfin());
		assertEquals(Integer.valueOf(trade.getBaseVersion()), Integer.valueOf(2));
		trade = tradeService.processCancel(new TradeReceiveCancelCommand(id));
		assertNotNull(trade);
		assertNotNull(trade.getId());
		assertNotNull(trade.getIsin());
		assertNotNull(trade.getCcy());
		assertNotNull(trade.getPrice());
		assertNotNull(trade.getQuantity());
		assertNotNull(trade.getCfin());
		assertEquals(Integer.valueOf(4),Integer.valueOf(trade.getBaseVersion()));
		
		long end = new Date().getTime() - start;
		logger.info("Finished processOneTransactionByReceiveAndCancelCommandInOneProcessTest in " + end + " ms");
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
		for (int i = 0; i < 10; i++) {
			executor.execute(new MyRunnableNotSent(tradeService));
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			// System.out.println("waiting for finish ...");
		}
		long end = new Date().getTime() - start;
		logger.info("Finished MyRunnableNotSent all threads in " + end + " ms");
		
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
	void loadAllEvents() throws ClassNotFoundException {
		long start = new Date().getTime();
		List<Event> l = tradeService.loadAllEvents();
		//assertTrue(CollectionUtils.isNotEmpty(l));
		long end = new Date().getTime() - start;
		logger.info("Finished loadAllEvents in " + end + " ms and retrieved l.size()=" + l.size());
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
	void processReceiveBookCommandMultiThreadTest() throws ClassNotFoundException {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		long start = new Date().getTime();
		for (int i = 0; i < 5; i++) {
			executor.execute(new MyRunnableBookCommand(tradeService));
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			// System.out.println("waiting for finish ...");
		}
		long end = new Date().getTime() - start;
		logger.info("Finished processReceiveBookCommandMultiThreadTest all threads in " + end + " ms");
	}

	public class MyRunnableBookCommand implements Runnable {
		TradeService tradeService;

		MyRunnableBookCommand(TradeService tradeService) {
			this.tradeService = tradeService;
		}

		@Override
		public void run() {
			Trade trade = tradeService.process(new TradeReceiveBookCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d,
					50));
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertNotNull(trade.getCfin());
			assertEquals(Integer.valueOf(2),Integer.valueOf(trade.getBaseVersion()));
		}
	}
	
	@Test
	void processReceiveBookAndCancelCommandMultiThreadTest() throws ClassNotFoundException {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		long start = new Date().getTime();
		for (int i = 0; i < 10; i++) {
			executor.execute(new MyRunnableBookAndCancel(tradeService));
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			// System.out.println("waiting for finish ...");
		}
		long end = new Date().getTime() - start;
		logger.info("Finished processReceiveBookAndCancelCommandMultiThreadTest all threads in " + end + " ms");
	}
	
	public class MyRunnableBookAndCancel implements Runnable {
		TradeService tradeService;

		MyRunnableBookAndCancel(TradeService tradeService) {
			this.tradeService = tradeService;
		}

		@Override
		public void run() {
			Trade trade = tradeService.process(new TradeReceiveBookCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d,
					50));
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertNotNull(trade.getCfin());
			assertEquals(Integer.valueOf(2),Integer.valueOf(trade.getBaseVersion()));
			trade = tradeService.processCancel(new TradeReceiveCancelCommand(trade.getId()));
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertNotNull(trade.getCfin());
			assertEquals(Integer.valueOf(4),Integer.valueOf(trade.getBaseVersion()));
		}
	}
	
	@Test
	void processNotSentMultiThreadTest() throws ClassNotFoundException {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		long start = new Date().getTime();
		for (int i = 0; i < 10; i++) {
			executor.execute(new MyRunnableNotSent(tradeService));
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			// System.out.println("waiting for finish ...");
		}
		long end = new Date().getTime() - start;
		logger.info("Finished processNotSentMultiThreadTest all threads in " + end + " ms");
	}
	
	public class MyRunnableNotSent implements Runnable {
		TradeService tradeService;

		MyRunnableNotSent(TradeService tradeService) {
			this.tradeService = tradeService;
		}

		@Override
		public void run() {
			Trade trade = tradeService.process(new TradeReceiveBookCommand(randomUUID().toString(), "FR0000", "EUR", 1000.0d, 50));
			assertNotNull(trade);
			assertNotNull(trade.getId());
			assertNotNull(trade.getIsin());
			assertNotNull(trade.getCcy());
			assertNotNull(trade.getPrice());
			assertNotNull(trade.getQuantity());
			assertEquals(Integer.valueOf(2),Integer.valueOf(trade.getBaseVersion()));
		}
	}
}
