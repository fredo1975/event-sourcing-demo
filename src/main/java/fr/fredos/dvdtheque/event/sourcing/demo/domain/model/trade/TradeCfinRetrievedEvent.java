package fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;

public class TradeCfinRetrievedEvent extends Event{
	private final String cfin;
	@JsonCreator
    public TradeCfinRetrievedEvent(@JsonProperty("aggregateId")String aggregateId,@JsonProperty("version") Integer version,@JsonProperty("cfin") String cfin) {
        super(aggregateId, version);
        this.cfin = checkNotNull(cfin);
    }
	public String getCfin() {
		return cfin;
	}
	@Override
	public String toString() {
		return "TradeCfinRetrievedEvent [cfin=" + cfin + "]";
	}

}
