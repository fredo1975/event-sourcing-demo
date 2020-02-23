package fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.joda.time.DateTime.now;
import static org.joda.time.DateTimeZone.UTC;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Aggregate;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;

public class Trade extends Aggregate{
	private UUID tradeId;
	private String isin;
    private String ccy;
    private double price;
    private int quantity;
    private String cfin;
    public Trade(UUID  id, UUID tradeId, String isin, String ccy, double price, int quantity) {
        super(id);
        validateID(tradeId);
        validateIsin(isin);
        validateCcy(isin);
        validatePrice(price);
        validateQuantity(quantity);
        TradeReceived tradeReceived = new TradeReceived(
                id, now(UTC), getNextVersion(), id, isin, ccy, price, quantity);
        applyNewEvent(tradeReceived);
    }

    public Trade(UUID id, List<Event> eventStream) {
        super(id, eventStream);
    }
    
    public void searchCfin(UUID  id, String isin, String ccy) {
    	String cfinRetrieved = "00000";
    	TradeCfinRetrieved tradeCfinRetrievedEvent = new TradeCfinRetrieved(
                getId(), now(UTC), getNextVersion(), cfinRetrieved);
        applyNewEvent(tradeCfinRetrievedEvent);
    }

    @SuppressWarnings("unused")
    public void apply(TradeReceived event) {
    	this.tradeId = event.getTradeId();
    	this.isin = event.getIsin();
        this.ccy = event.getCcy();
        this.price = event.getPrice();
        this.quantity = event.getQuantity();
    }

    @SuppressWarnings("unused")
    private void apply(TradeCfinRetrieved event) {
        this.cfin = event.getCfin();
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

	public String getCfin() {
		return cfin;
	}

	private void validateID(UUID id) {
		checkNotNull(id);
    }

    private void validateIsin(String isin) {
        checkNotNull(StringUtils.isNotBlank(isin));
    }
    
    private void validateCcy(String ccy) {
    	checkNotNull(StringUtils.isNotBlank(ccy));
    }
    private void validatePrice(double price) {
		checkNotNull(price);
    }
    private void validateQuantity(int qty) {
		checkNotNull(qty);
    }
}
