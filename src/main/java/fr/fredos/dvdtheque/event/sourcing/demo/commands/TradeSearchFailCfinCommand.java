package fr.fredos.dvdtheque.event.sourcing.demo.commands;

import static com.google.common.base.Preconditions.checkNotNull;

public class TradeSearchFailCfinCommand implements TradeCommand{
	private final String id;
	private final String errorMessage;
	public TradeSearchFailCfinCommand(String id, String errorMessage) {
		this.id = checkNotNull(id);
		this.errorMessage = checkNotNull(errorMessage);
    }
	
	public String getId() {
		return id;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
}
