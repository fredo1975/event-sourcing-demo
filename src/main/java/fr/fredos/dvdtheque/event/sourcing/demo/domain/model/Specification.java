package fr.fredos.dvdtheque.event.sourcing.demo.domain.model;

public interface Specification<T> {

    boolean isSatisfiedBy(T value);

}
