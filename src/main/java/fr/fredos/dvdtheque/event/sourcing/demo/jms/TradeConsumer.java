package fr.fredos.dvdtheque.event.sourcing.demo.jms;

import static fr.fredos.dvdtheque.event.sourcing.demo.JmsConfiguration.TRADE_QUEUE;
import static java.util.UUID.randomUUID;

import java.util.concurrent.CountDownLatch;

import javax.jms.JMSException;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeReceiveBookCommand;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeJms;
import fr.fredos.dvdtheque.event.sourcing.demo.trade.service.TradeService;

@Component
public class TradeConsumer {
	protected Logger logger = LoggerFactory.getLogger(TradeConsumer.class);
	@Autowired
	private TradeService tradeService;
	
	private CountDownLatch latch = new CountDownLatch(20000);
	public CountDownLatch getLatch() {
		return latch;
	}


	@JmsListener(destination = TRADE_QUEUE)
    public void receiveMessage(@Payload TradeJms trade,
                               @Headers MessageHeaders headers,
                               Message<?> message, 
                               Session session) throws JMSException {
		/*
		logger.info("received <" + trade + ">");
		TextMessage tm = session.createTextMessage();
		logger.info("received tm <" + tm + ">");
		
		logger.info("- - - - - - - - - - - - - - - - - - - - - - - -");
		logger.info("######          Message Details           #####");
		logger.info("- - - - - - - - - - - - - - - - - - - - - - - -");
		logger.info("headers: " + headers);
		logger.info("message: " + message);
		logger.info("session: " + session);
		logger.info("- - - - - - - - - - - - - - - - - - - - - - - -");
		*/
		tradeService.process(new TradeReceiveBookCommand(randomUUID().toString(), trade.getIsin(), trade.getCcy(), trade.getPrice(),trade.getQuantity()));
		latch.countDown();
    }
}
