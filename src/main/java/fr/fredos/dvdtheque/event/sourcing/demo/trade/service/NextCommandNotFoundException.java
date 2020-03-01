package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import static java.lang.String.format;

import fr.fredos.dvdtheque.event.sourcing.demo.commands.TradeCommand;

public class NextCommandNotFoundException extends RuntimeException{
	public NextCommandNotFoundException(final String id,final Class<? extends TradeCommand> clazz) {
		super(format("Unable to find command '%s' for aggregate with id '%s'", clazz,id));
	}
}
