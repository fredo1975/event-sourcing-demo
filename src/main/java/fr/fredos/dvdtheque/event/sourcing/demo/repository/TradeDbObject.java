package fr.fredos.dvdtheque.event.sourcing.demo.repository;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TradeDbObject implements Serializable{
	private static final long serialVersionUID = -6808313573354156105L;
	private String eventIdentifier;
	private String payload;
	private String payloadType;
	private String aggregateIdentifier;
	private Integer sequenceNumber;
	private LocalDateTime timestamp;
	public TradeDbObject() {
		super();
	}
	public TradeDbObject(String aggregateIdentifier) {
		super();
		this.aggregateIdentifier = aggregateIdentifier;
	}

	public String getEventIdentifier() {
		return eventIdentifier;
	}
	public void setEventIdentifier(String eventIdentifier) {
		this.eventIdentifier = eventIdentifier;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	public String getPayloadType() {
		return payloadType;
	}
	public void setPayloadType(String payloadType) {
		this.payloadType = payloadType;
	}
	public String getAggregateIdentifier() {
		return aggregateIdentifier;
	}
	public void setAggregateIdentifier(String aggregateIdentifier) {
		this.aggregateIdentifier = aggregateIdentifier;
	}
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
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
		TradeDbObject other = (TradeDbObject) obj;
		if (eventIdentifier == null) {
			if (other.eventIdentifier != null)
				return false;
		} else if (!eventIdentifier.equals(other.eventIdentifier))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TradeDbObject [eventIdentifier=" + eventIdentifier + ", payload=" + payload + ", payloadType="
				+ payloadType + ", aggregateIdentifier=" + aggregateIdentifier + ", sequenceNumber=" + sequenceNumber
				+ ", timestamp=" + timestamp + "]";
	}
	
}
