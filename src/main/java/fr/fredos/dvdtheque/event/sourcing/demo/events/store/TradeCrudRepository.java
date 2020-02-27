package fr.fredos.dvdtheque.event.sourcing.demo.events.store;

import org.springframework.data.repository.CrudRepository;

import fr.fredos.dvdtheque.event.sourcing.demo.repository.TradeCrudEntity;

public interface TradeCrudRepository extends CrudRepository<TradeCrudEntity, Long>{

}
