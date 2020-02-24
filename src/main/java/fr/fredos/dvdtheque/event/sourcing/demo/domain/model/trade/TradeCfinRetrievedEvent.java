package fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;

public class TradeCfinRetrievedEvent extends Event{
	private final String cfin;

    public TradeCfinRetrievedEvent(UUID aggregateId, int version, String cfin) {
        super(aggregateId, version);
        this.cfin = checkNotNull(cfin);
    }

	public String getCfin() {
		return cfin;
	}

}
