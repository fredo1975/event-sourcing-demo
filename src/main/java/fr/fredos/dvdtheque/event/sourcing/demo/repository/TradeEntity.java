package fr.fredos.dvdtheque.event.sourcing.demo.repository;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TRADE_ENTITY")
public class TradeEntity {
	@Id
	private String aggregateId;

	protected TradeEntity() {
	}

	public TradeEntity(String aggregateId) {
		super();
		this.aggregateId = aggregateId;
	}

	public String getAggregateId() {
		return aggregateId;
	}

}
