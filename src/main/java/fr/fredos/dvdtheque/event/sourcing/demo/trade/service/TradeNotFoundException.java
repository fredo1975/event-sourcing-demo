package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import static java.lang.String.format;

public class TradeNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -9143997159257862671L;

	public TradeNotFoundException(String id) {
		super(format("Trade with id '%s' could not be found", id));
	}
	
	public TradeNotFoundException(long id) {
		super(format("Trade with id '%s' could not be found", id));
	}
}
