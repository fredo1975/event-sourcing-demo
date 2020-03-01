package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import static java.lang.String.format;

public class SerializeException extends RuntimeException{
	private static final long serialVersionUID = -9143997159257862671L;

	public SerializeException(final String id,final String clazz) {
		super(format("Unable to serialize payload type '%s' with aggregate with id '%s'", clazz,id));
	}
}
