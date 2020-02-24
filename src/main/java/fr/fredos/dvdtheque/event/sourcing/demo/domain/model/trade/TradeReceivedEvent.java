package fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;

public class TradeReceivedEvent extends Event{
	private UUID tradeId;
	private String isin;
    private String ccy;
    private double price;
    private int quantity;
    @JsonCreator
    public TradeReceivedEvent(@JsonProperty("aggregateId")UUID aggregateId,@JsonProperty("version") int version,
    		@JsonProperty("tradeId") UUID tradeId,
    		@JsonProperty("isin") String isin,
    		@JsonProperty("ccy") String ccy,
    		@JsonProperty("price") double price,
    		@JsonProperty("quantity")int quantity) {
        super(aggregateId, version);
        this.tradeId = checkNotNull(tradeId);
        this.isin = checkNotNull(isin);
        this.ccy = checkNotNull(ccy);
        this.price = checkNotNull(price);
        this.quantity = checkNotNull(quantity);
    }
	
	public UUID getTradeId() {
		return tradeId;
	}

	public String getIsin() {
		return isin;
	}
	public String getCcy() {
		return ccy;
	}
	public double getPrice() {
		return price;
	}
	public int getQuantity() {
		return quantity;
	}

}
