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
    private String errorMessage;
    public Trade(String  id) {
    	super(id);
    }
    public Trade(String  id, String isin, String ccy, double price, int quantity) {
        super(id);
        validateIsin(isin);
        validateCcy(isin);
        validatePrice(price);
        validateQuantity(quantity);
        TradeReceivedBookEvent tradeReceived = new TradeReceivedBookEvent(
                id, getNextVersion(), id, isin, ccy, price, quantity);
        applyNewEvent(tradeReceived);
    }

    public Trade(String id, List<Event> eventStream) {
        super(id, eventStream);
    }
    
    public void cancel() {
    	TradeReceivedCancelEvent tradeReceivedCancelEvent = new TradeReceivedCancelEvent(
                getId(), getNextVersion());
        applyNewEvent(tradeReceivedCancelEvent);
    }
    public void searchCfin(String isin,String ccy) {
    	String cfinRetrieved = "00000";
    	TradeCfinRetrievedEvent tradeCfinRetrievedEvent = new TradeCfinRetrievedEvent(
                getId(), getNextVersion(), isin,ccy,cfinRetrieved);
        applyNewEvent(tradeCfinRetrievedEvent);
    }
    
    public void searchFailCfin(String errorMessage) {
    	TradeCfinRetrieveFailedEvent tradeCfinRetrievedEvent = new TradeCfinRetrieveFailedEvent(
                getId(), getNextVersion(),getIsin(),getCcy(), errorMessage);
        applyNewEvent(tradeCfinRetrievedEvent);
    }
    
    public void send() {
    	TradeSentEvent tradeSendedEvent = new TradeSentEvent(
                getId(), getNextVersion());
        applyNewEvent(tradeSendedEvent);
    }

    @SuppressWarnings("unused")
    public void apply(TradeReceivedBookEvent event) {
    	this.isin = event.getIsin();
        this.ccy = event.getCcy();
        this.price = event.getPrice();
        this.quantity = event.getQuantity();
    }
    @SuppressWarnings("unused")
    public void apply(TradeReceivedCancelEvent event) {
    	
    }
    @SuppressWarnings("unused")
    private void apply(TradeCfinRetrievedEvent event) {
        this.cfin = event.getCfin();
    }
    
    @SuppressWarnings("unused")
    private void apply(TradeSentEvent event) {
        //this.cfin = event.getCfin();
    }
    @SuppressWarnings("unused")
    private void apply(TradeCfinRetrieveFailedEvent event) {
        this.errorMessage = event.getErrorMessage();
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

    public String getErrorMessage() {
		return errorMessage;
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
