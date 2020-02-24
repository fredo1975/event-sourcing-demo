package fr.fredos.dvdtheque.event.sourcing.demo.domain.model;

public class OptimisticLockingException extends RuntimeException {

    private static final long serialVersionUID = 3719836063805573849L;

	public OptimisticLockingException(String message) {
        super(message);
    }
}
