package fr.fredos.dvdtheque.event.sourcing.demo.jms;

import static fr.fredos.dvdtheque.event.sourcing.demo.JmsConfiguration.TRADE_QUEUE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeJms;

@Service
public class TradeSender {
	protected Logger logger = LoggerFactory.getLogger(TradeSender.class);
    @Autowired
    private JmsTemplate jmsTemplate; 
    public void send(TradeJms trade) {
    	//logger.info("sending with convertAndSend() to queue <" + trade + ">");
        jmsTemplate.convertAndSend(TRADE_QUEUE, trade);
    }
}
