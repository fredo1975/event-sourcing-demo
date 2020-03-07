package fr.fredos.dvdtheque.event.sourcing.demo.jms;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade.TradeJms;

@Component
public class TradeJmsMessageConverter implements MessageConverter {
	protected Logger logger = LoggerFactory.getLogger(TradeJmsMessageConverter.class);
	@Autowired
	private ObjectMapper mapper;

	@Override
	public javax.jms.Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		TradeJms trade = (TradeJms) object;
	    String payload = null;
	    try {
	      payload = mapper.writeValueAsString(trade);
	      //logger.info("outbound json='{}'", payload);
	    } catch (JsonProcessingException e) {
	    	logger.error("error converting form person", e);
	    }
	    TextMessage message = session.createTextMessage();
	    message.setText(payload);
	    return message;
	}

	@Override
	public Object fromMessage(javax.jms.Message message) throws JMSException, MessageConversionException {
		TextMessage textMessage = (TextMessage) message;
	    String payload = textMessage.getText();
	    //logger.info("inbound json='{}'", payload);

	    TradeJms trade = null;
	    try {
	    	trade = mapper.readValue(payload, TradeJms.class);
	    } catch (Exception e) {
	    	logger.error("error converting to person", e);
	    }
	    return trade;
	}
}
