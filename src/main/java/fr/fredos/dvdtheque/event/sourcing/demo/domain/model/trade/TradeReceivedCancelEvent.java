package fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;

public class TradeReceivedCancelEvent extends Event{
	@JsonCreator
    public TradeReceivedCancelEvent(@JsonProperty("aggregateId")String aggregateId,@JsonProperty("version") Integer version) {
        super(aggregateId, version);
    }
	
}
