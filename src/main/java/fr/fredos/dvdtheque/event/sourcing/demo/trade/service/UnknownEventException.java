package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import static java.lang.String.format;

public class UnknownEventException extends RuntimeException {
	private static final long serialVersionUID = 7013764356682587122L;

	public UnknownEventException(final String id,final String clazz) {
		super(format("Unknown payload type '%s' with aggregate with id '%s'", clazz,id));
	}
}
