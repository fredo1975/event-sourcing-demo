package fr.fredos.dvdtheque.event.sourcing.demo.domain.model;

import java.io.Serializable;

public class EventKey implements Serializable {
	private static final long serialVersionUID = 5640692645498953475L;
	private final String eventIdentifier;
	private final String payloadType;
	public EventKey(String eventIdentifier, String payloadType) {
		super();
		this.eventIdentifier = eventIdentifier;
		this.payloadType = payloadType;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventIdentifier == null) ? 0 : eventIdentifier.hashCode());
		result = prime * result + ((payloadType == null) ? 0 : payloadType.hashCode());
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
		EventKey other = (EventKey) obj;
		if (eventIdentifier == null) {
			if (other.eventIdentifier != null)
				return false;
		} else if (!eventIdentifier.equals(other.eventIdentifier))
			return false;
		if (payloadType == null) {
			if (other.payloadType != null)
				return false;
		} else if (!payloadType.equals(other.payloadType))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "EventKey [eventIdentifier=" + eventIdentifier + ", payloadType=" + payloadType + "]";
	}
	
}
