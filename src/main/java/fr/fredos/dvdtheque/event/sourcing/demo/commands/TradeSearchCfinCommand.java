package fr.fredos.dvdtheque.event.sourcing.demo.commands;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

public class TradeSearchCfinCommand {
	private final UUID id;
	private final String isin;
	private final String ccy;
	public TradeSearchCfinCommand(UUID id, String isin, String ccy) {
		this.id = checkNotNull(id);
		this.isin = checkNotNull(isin);
		this.ccy = checkNotNull(ccy);
    }
	
	public UUID getId() {
		return id;
	}
	public String getIsin() {
		return isin;
	}
	public String getCcy() {
		return ccy;
	}
	

}
