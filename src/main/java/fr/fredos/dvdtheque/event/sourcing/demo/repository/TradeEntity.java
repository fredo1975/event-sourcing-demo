package fr.fredos.dvdtheque.event.sourcing.demo.repository;

import java.io.Serializable;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.micrometer.core.lang.NonNull;

@Entity
@Table(name = "DOMAIN_EVENT_ENTRY")
public class TradeEntity implements Serializable{
	private static final long serialVersionUID = -14288181758812657L;
	/*@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "next_val")
	@SequenceGenerator(name="next_val", sequenceName = "next_val")
	@Column(name = "GLOBAL_INDEX", updatable = false, nullable = false)
	public Integer globalIndex;*/
	@Id
	@Column(name = "EVENT_IDENTIFIER")
	@NotNull
	private String eventIdentifier;
	@Column(name = "PAYLOAD")
	@NotNull
	private String payload;
	@Column(name = "PAYLOAD_TYPE")
	@NotNull
	private String payloadType;
	@Column(name = "AGGREGATE_IDENTIFIER")
	@NotNull
	private String aggregateIdentifier;
	@Column(name = "SEQUENCE_NUMBER")
	@NotNull
	private Integer sequenceNumber;
	@Column(name = "TYPE")
	private String type;
	@Column(name = "TIMESTAMP")
	@NotNull
	private ZonedDateTime timeStamp;
	
	protected TradeEntity() {
	}

	public TradeEntity(String aggregateIdentifier) {
		super();
		this.aggregateIdentifier = aggregateIdentifier;
	}
	public TradeEntity(final String aggregateIdentifier,
			final Integer sequenceNumber,
			final String eventIdentifier,
			final String payload,
			final String payloadType,
			final ZonedDateTime timeStamp) {
		super();
		this.aggregateIdentifier = aggregateIdentifier;
		this.sequenceNumber = sequenceNumber;
		this.timeStamp = timeStamp;
	}
	public String getEventIdentifier() {
		return eventIdentifier;
	}

	public void setEventIdentifier(String eventIdentifier) {
		this.eventIdentifier = eventIdentifier;
	}
/*
	public Integer getGlobalIndex() {
		return globalIndex;
	}*/

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
/*
	public void setGlobalIndex(Integer globalIndex) {
		this.globalIndex = globalIndex;
	}*/

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

	public ZonedDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(ZonedDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventIdentifier == null) ? 0 : eventIdentifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TradeEntity other = (TradeEntity) obj;
		if (eventIdentifier == null) {
			if (other.eventIdentifier != null)
				return false;
		} else if (!eventIdentifier.equals(other.eventIdentifier))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TradeEntity [globalIndex=" + /*globalIndex +*/ ", eventIdentifier=" + eventIdentifier + ", payload="
				+ payload + ", payloadType=" + payloadType + ", aggregateIdentifier=" + aggregateIdentifier
				+ ", sequenceNumber=" + sequenceNumber + ", type=" + type + "]";
	}

	

}
