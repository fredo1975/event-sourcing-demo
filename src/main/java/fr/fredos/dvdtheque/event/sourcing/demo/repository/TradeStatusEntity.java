package fr.fredos.dvdtheque.event.sourcing.demo.repository;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
@Entity
@Table(name = "DOMAIN_STATUS_EVENT_ENTRY")
public class TradeStatusEntity implements Serializable{
	private static final long serialVersionUID = -8836967630256630193L;
	@Id
	@Column(name = "AGGREGATE_IDENTIFIER")
	public String aggregateIdentifier;
	@Column(name = "LAST_PAYLOAD_TYPE")
	@NotNull
	private String lastPayloadType;
	
	public TradeStatusEntity() {
		super();
	}

	public String getLastPayloadType() {
		return lastPayloadType;
	}

	public void setLastPayloadType(String lastPayloadType) {
		this.lastPayloadType = lastPayloadType;
	}

	public String getAggregateIdentifier() {
		return aggregateIdentifier;
	}

	public void setAggregateIdentifier(String aggregateIdentifier) {
		this.aggregateIdentifier = aggregateIdentifier;
	}
	
}
