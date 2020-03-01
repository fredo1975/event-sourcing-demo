package fr.fredos.dvdtheque.event.sourcing.demo.commands;

import static com.google.common.base.Preconditions.checkNotNull;

public class TradeSendCommand implements TradeCommand{
	private final String id;
	public TradeSendCommand(String id) {
		this.id = checkNotNull(id);
    }
	public String getId() {
		return id;
	}
	
}
