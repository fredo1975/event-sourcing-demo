package fr.fredos.dvdtheque.event.sourcing.demo.jms;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import fr.fredos.dvdtheque.event.sourcing.demo.JmsConfiguration;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeJms;

@SpringBootTest(classes = { fr.fredos.dvdtheque.event.sourcing.demo.EventSourcingSpringBootApplication.class,
		JmsConfiguration.class})
@ActiveProfiles("mybatis")
public class TradeConsumerTest {
	protected Logger logger = LoggerFactory.getLogger(TradeConsumerTest.class);
	@Autowired
	private TradeSender tradeSender;
	@Autowired
	private TradeConsumer tradeConsumer;
	@Test
	public void processReceiveBookCommandTest() throws InterruptedException {
		final TradeJms trade = new TradeJms("FR0000", "EUR", 1000.0d, 50);
		tradeSender.send(trade);
		while(tradeConsumer.getLatch().getCount() != 0) {
			//logger.info("waiting to consume message");
		}
		logger.info("message consumed");
	}
	
	@Test
	public void processReceiveBookCommandMultiThreadTest() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		long start = new Date().getTime();
		for (int i = 0; i < 10; i++) {
			executor.execute(new MyRunnableBookCommand(tradeSender));
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			// System.out.println("waiting for finish ...");
		}
		while(tradeConsumer.getLatch().getCount() != 0) {
			//logger.info("waiting to consume message");
		}
		long end = new Date().getTime() - start;
		logger.info("Finished processReceiveBookCommandMultiThreadTest all threads in " + end + " ms");
	}
	
	public class MyRunnableBookCommand implements Runnable {
		TradeSender tradeSender;

		MyRunnableBookCommand(TradeSender tradeSender) {
			this.tradeSender = tradeSender;
		}

		@Override
		public void run() {
			final TradeJms trade = new TradeJms("FR0000", "EUR", 1000.0d, 50);
			tradeSender.send(trade);
			
		}
	}
}
