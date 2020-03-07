package fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;

public class TradeSentEvent extends Event{
	@JsonCreator
	public TradeSentEvent(@JsonProperty("aggregateId")String aggregateId,
			@JsonProperty("version") Integer version) {
        super(aggregateId, version);
    }
}
