package fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

import org.joda.time.DateTime;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;

public class TradeReceived extends Event{
	private UUID tradeId;
	private String isin;
    private String ccy;
    private double price;
    private int quantity;
    public TradeReceived(UUID aggregateId, DateTime timestamp, int version,UUID tradeId,String isin,String ccy,double price,int quantity) {
        super(aggregateId, timestamp, version);
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
