package fr.fredos.dvdtheque.event.sourcing.demo.commands;

import static com.google.common.base.Preconditions.checkNotNull;

public class TradeReceiveCommand {
	private String tradeId;
	private String isin;
    private String ccy;
    private double price;
    private int quantity;
    
    public TradeReceiveCommand(String tradeId, String isin, String ccy,double price, int quantity) {
    	this.tradeId = checkNotNull(tradeId);
        this.isin = checkNotNull(isin);
        this.ccy = checkNotNull(ccy);
        this.price = checkNotNull(price);
        this.quantity = checkNotNull(quantity);
    }

	public String getTradeId() {
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
