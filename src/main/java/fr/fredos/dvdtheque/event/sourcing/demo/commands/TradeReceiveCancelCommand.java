package fr.fredos.dvdtheque.event.sourcing.demo.commands;

import static com.google.common.base.Preconditions.checkNotNull;

public class TradeReceiveCancelCommand {
	private String id;
	public TradeReceiveCancelCommand(String id) {
    	this.id = checkNotNull(id);
    }
	public String getId() {
		return id;
	}

}
