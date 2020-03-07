package fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade;

import static com.google.common.base.Preconditions.checkNotNull;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;

public class TradeCfinRetrievedEvent extends Event{
	private final String cfin;
	private String isin;
    private String ccy;
	@JsonCreator
    public TradeCfinRetrievedEvent(@JsonProperty("aggregateId")String aggregateId,
    		@JsonProperty("version") Integer version,
    		@JsonProperty("isin")String isin,
    		@JsonProperty("ccy")String ccy,
    		@JsonProperty("cfin") String cfin) {
        super(aggregateId, version);
        this.cfin = checkNotNull(cfin);
        this.isin = isin;
        this.ccy = ccy;
    }
	public String getCfin() {
		return cfin;
	}
	public String getIsin() {
		return isin;
	}
	public String getCcy() {
		return ccy;
	}
	
}
