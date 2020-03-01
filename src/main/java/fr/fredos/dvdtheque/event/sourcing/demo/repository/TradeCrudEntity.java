package fr.fredos.dvdtheque.event.sourcing.demo.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
@Entity
@Table(name = "TRADE_CRUD_ENTITY")
public class TradeCrudEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "next_val")
	@SequenceGenerator(name="next_val", sequenceName = "next_val")
	public Long id;
	@Column(name = "ISIN")
	private String isin;
	@Column(name = "CCY")
    private String ccy;
	@Column(name = "PRICE")
    private double price;
	@Column(name = "QTY")
    private int quantity;
	@Column(name = "CFIN")
    private String cfin;
	@Column(name = "SENT")
    private boolean sent;
	
	public TradeCrudEntity() {
		super();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}
	
}
