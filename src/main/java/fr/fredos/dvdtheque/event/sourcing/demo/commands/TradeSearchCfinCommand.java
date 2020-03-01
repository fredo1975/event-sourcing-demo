package fr.fredos.dvdtheque.event.sourcing.demo.commands;

import static com.google.common.base.Preconditions.checkNotNull;

public class TradeSearchCfinCommand implements TradeCommand{
	private final String id;
	private final String isin;
	private final String ccy;
	public TradeSearchCfinCommand(String id, String isin, String ccy) {
		this.id = checkNotNull(id);
		this.isin = checkNotNull(isin);
		this.ccy = checkNotNull(ccy);
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
	

}
