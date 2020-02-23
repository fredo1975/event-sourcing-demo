package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import static java.lang.String.format;

import java.util.UUID;

public class TradeNotFoundException extends RuntimeException {

    public TradeNotFoundException(UUID id) {
        super(format("Trade with id '%s' could not be found", id));
    }
}
