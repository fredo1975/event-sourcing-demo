package fr.fredos.dvdtheque.event.sourcing.demo.trade.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Retrier {

    //private final List<Class<? extends Exception>> retriableExceptions;
    @Value("${retrier.maxAttempts}")
    private int maxAttempts;
/*
    public <T> T get(Supplier<T> supplier) {
        for (int attempt = 1; ; attempt++) {
            try {
                return supplier.get();
            } catch (Exception exception) {
                if (!isRetriable(exception) || attempt == maxAttempts) throw exception;
            }
        }
    }*/
/*
    private boolean isRetriable(Exception exception) {
        return retriableExceptions.stream().anyMatch(e -> e.isAssignableFrom(exception.getClass()));
    }*/
}
