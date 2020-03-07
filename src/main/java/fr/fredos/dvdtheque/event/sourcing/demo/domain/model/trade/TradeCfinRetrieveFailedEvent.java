package fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;

public class TradeCfinRetrieveFailedEvent extends Event{
	private String errorMessage;
	private String isin;
    private String ccy;
	@JsonCreator
    public TradeCfinRetrieveFailedEvent(@JsonProperty("aggregateId")String aggregateId,
    		@JsonProperty("version") Integer version,
    		@JsonProperty("isin") String isin,
    		@JsonProperty("ccy") String ccy,
    		@JsonProperty("errorMessage") String errorMessage) {
        super(aggregateId, version);
        this.errorMessage = errorMessage;
    }
	public String getErrorMessage() {
		return errorMessage;
	}
	public String getIsin() {
		return isin;
	}
	public String getCcy() {
		return ccy;
	}
	
}
