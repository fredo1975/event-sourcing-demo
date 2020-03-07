package fr.fredos.dvdtheque.event.sourcing.demo.commands;

import static com.google.common.base.Preconditions.checkNotNull;

public class TradeSearchFailCfinCommand implements TradeCommand{
	private final String id;
	private String isin;
    private String ccy;
	private final String errorMessage;
	public TradeSearchFailCfinCommand(String id, String isin,String ccy,String errorMessage) {
		this.id = checkNotNull(id);
		this.errorMessage = checkNotNull(errorMessage);
    }
	
	public String getId() {
		return id;
	}

	public String getIsin() {
		return isin;
	}

	public String getCcy() {
		return ccy;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
}
