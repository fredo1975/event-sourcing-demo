package fr.fredos.dvdtheque.event.sourcing.demo.domain.model;

public class OptimisticLockingException extends RuntimeException {

    public OptimisticLockingException(String message) {
        super(message);
    }
}
