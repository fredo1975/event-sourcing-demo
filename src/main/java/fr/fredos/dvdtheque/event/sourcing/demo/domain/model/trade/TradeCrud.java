package fr.fredos.dvdtheque.event.sourcing.demo.domain.model.trade;

public class TradeCrud {
	public long id;
	private String isin;
    private String ccy;
    private double price;
    private int quantity;
    private String cfin;
    
    
	public TradeCrud() {
		super();
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public String getCfin() {
		return cfin;
	}
	public void setCfin(String cfin) {
		this.cfin = cfin;
	}
}
