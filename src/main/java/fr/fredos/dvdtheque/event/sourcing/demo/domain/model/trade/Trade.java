package fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Aggregate;
import fr.fredos.dvdtheque.event.sourcing.demo.domain.model.Event;

public class Trade extends Aggregate{
	private String isin;
    private String ccy;
    private double price;
    private int quantity;
    private String cfin;
    public Trade(String  id, String isin, String ccy, double price, int quantity) {
        super(id);
        validateIsin(isin);
        validateCcy(isin);
        validatePrice(price);
        validateQuantity(quantity);
        TradeReceivedEvent tradeReceived = new TradeReceivedEvent(
                id, getNextVersion(), id, isin, ccy, price, quantity);
        applyNewEvent(tradeReceived);
    }

    public Trade(String id, List<Event> eventStream) {
        super(id, eventStream);
    }
    
    public void searchCfin(String  id, String isin, String ccy) {
    	String cfinRetrieved = "00000";
    	TradeCfinRetrievedEvent tradeCfinRetrievedEvent = new TradeCfinRetrievedEvent(
                getId(), getNextVersion(), cfinRetrieved);
        applyNewEvent(tradeCfinRetrievedEvent);
    }
    
    public void send(String id) {
    	TradeSentEvent tradeSendedEvent = new TradeSentEvent(
                getId(), getNextVersion());
        applyNewEvent(tradeSendedEvent);
    }

    @SuppressWarnings("unused")
    public void apply(TradeReceivedEvent event) {
    	this.isin = event.getIsin();
        this.ccy = event.getCcy();
        this.price = event.getPrice();
        this.quantity = event.getQuantity();
    }

    @SuppressWarnings("unused")
    private void apply(TradeCfinRetrievedEvent event) {
        this.cfin = event.getCfin();
    }
    
    @SuppressWarnings("unused")
    private void apply(TradeSentEvent event) {
        //this.cfin = event.getCfin();
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
