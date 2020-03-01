package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import static java.lang.String.format;

public class DeserializeException extends RuntimeException{
	private static final long serialVersionUID = 4296262144524271626L;

	public DeserializeException(final String id,final String clazz) {
		super(format("Unable to deserialize payload type '%s' with aggregate with id '%s'", clazz,id));
	}
}
