package fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

import org.joda.time.DateTime;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;

public class TradeCfinRetrieved extends Event{
	private final String cfin;

    public TradeCfinRetrieved(UUID aggregateId, DateTime timestamp, int version, String cfin) {
        super(aggregateId, timestamp, version);
        this.cfin = checkNotNull(cfin);
    }

	public String getCfin() {
		return cfin;
	}

}
