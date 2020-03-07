package fr.fredos.dvdtheque.event.sourcing.demo.commands;

import static com.google.common.base.Preconditions.checkNotNull;

public class TradeEnterManualCfinCommand implements TradeCommand{
	private final String id;
	private final String cfin;
	public TradeEnterManualCfinCommand(String id, String cfin) {
		this.id = checkNotNull(id);
		this.cfin = checkNotNull(cfin);
    }
	public String getId() {
		return id;
	}
	public String getCfin() {
		return cfin;
	}
}
