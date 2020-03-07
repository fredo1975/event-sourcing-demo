package fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade;

import java.io.Serializable;

public class TradeJms implements Serializable {
	private static final long serialVersionUID = -1668741712282486878L;
	private String isin;
    private String ccy;
    private double price;
    private int quantity;
	public TradeJms() {
		super();
	}
	
	public TradeJms(String isin, String ccy, double price, int quantity) {
		super();
		this.isin = isin;
		this.ccy = ccy;
		this.price = price;
		this.quantity = quantity;
	}

	public String getIsin() {
		return isin;
	}
	public void setIsin(String isin) {
		this.isin = isin;
	}
	public String getCcy() {
		return ccy;
	}
	public void setCcy(String ccy) {
		this.ccy = ccy;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "TradeJms [isin=" + isin + ", ccy=" + ccy + ", price=" + price + ", quantity=" + quantity + "]";
	}
    
    
}
