package fr.fredos.dvdtheque.event.sourcing.demo.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "DOMAIN_EVENT_ENTRY")
public class TradeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "next_val")
	@SequenceGenerator(name="next_val", sequenceName = "next_val")
	@Column(name = "GLOBAL_INDEX", updatable = false, nullable = false)
	public Integer globalIndex;
	@Column(name = "EVENT_IDENTIFIER")
	private String eventIdentifier;
	@Lob
	@Column(name = "PAYLOAD", columnDefinition="BLOB")
	private String payload;
	@Column(name = "PAYLOAD_TYPE")
	private String payloadType;
	
	@Column(name = "AGGREGATE_IDENTIFIER")
	private String aggregateIdentifier;
	@Column(name = "SEQUENCE_NUMBER")
	private Integer sequenceNumber;
	@Column(name = "TYPE")
	private String type;
	

	protected TradeEntity() {
	}

	public TradeEntity(String aggregateIdentifier) {
		super();
		this.aggregateIdentifier = aggregateIdentifier;
	}

	public String getEventIdentifier() {
		return eventIdentifier;
	}

	public void setEventIdentifier(String eventIdentifier) {
		this.eventIdentifier = eventIdentifier;
	}

	public Integer getGlobalIndex() {
		return globalIndex;
	}

	public String getPayload() {
		return payload;
	}

	public String getPayloadType() {
		return payloadType;
	}

	public String getAggregateIdentifier() {
		return aggregateIdentifier;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public String getType() {
		return type;
	}

	public void setGlobalIndex(Integer globalIndex) {
		this.globalIndex = globalIndex;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public void setPayloadType(String payloadType) {
		this.payloadType = payloadType;
	}

	public void setAggregateIdentifier(String aggregateIdentifier) {
		this.aggregateIdentifier = aggregateIdentifier;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public void setType(String type) {
		this.type = type;
	}

	

}
