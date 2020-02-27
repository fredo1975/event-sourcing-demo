package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

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

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeCrud;
import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeCrudEntity;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {fr.fredos.dvdtheque.event.sourcing.demo.EventSourcingSpringBootApplication.class})
@ActiveProfiles("jpa")
public class TradeCrudServiceImplTest {
	@Autowired
	TradeCrudService tradeCrudService;
	
	@Test
	void tradeProcess() {
		long start = new Date().getTime();
		TradeCrud trade = new TradeCrud();
		trade.setIsin("FR0000");
		trade.setCcy("EUR");
		trade.setPrice(1000.0d);
		trade.setQuantity(50);
		TradeCrudEntity tradeCrudEntity = tradeCrudService.save(trade);
		assertNotNull(tradeCrudEntity);
		tradeCrudEntity.setCfin("00000");
		tradeCrudEntity = tradeCrudService.update(tradeCrudEntity);
		assertNotNull(tradeCrudEntity);
		assertNotNull(tradeCrudEntity.getCfin());
		long end = new Date().getTime()-start;
		System.out.println("Finished tradeProcess in "+end + " ms");
	}
	
	@Test
	void tradeReceiveCommandMultiThreadTest() throws TradeNotFoundException, SerializeException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		long start = new Date().getTime();
		for(int i=0;i<1000;i++) {
			executor.execute(new MyRunnable(tradeCrudService));
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		long end = new Date().getTime()-start;
		System.out.println("Finished all threads in "+end + " ms");
	}
	
	public class MyRunnable implements Runnable {
		TradeCrudService tradeCrudService;
		MyRunnable(TradeCrudService tradeCrudService) {
			this.tradeCrudService = tradeCrudService;
		}
		@Override
		public void run() {
			TradeCrud trade = new TradeCrud();
			trade.setIsin("FR0000");
			trade.setCcy("EUR");
			trade.setPrice(1000.0d);
			trade.setQuantity(50);
			TradeCrudEntity tradeCrudEntity = tradeCrudService.save(trade);
			assertNotNull(tradeCrudEntity);
			tradeCrudEntity.setCfin("00000");
			tradeCrudEntity = tradeCrudService.update(tradeCrudEntity);
			assertNotNull(tradeCrudEntity);
			assertNotNull(tradeCrudEntity.getCfin());
		}
	}
}
