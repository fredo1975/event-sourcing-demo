package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import static java.lang.String.format;

import java.util.UUID;

public class SerializeException extends RuntimeException{
	private static final long serialVersionUID = -9143997159257862671L;

	public SerializeException(UUID id) {
		super(format("Unable to serialize aggregate with id '%s'", id));
	}
}
