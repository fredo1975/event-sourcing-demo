package fr.fredos.dvdtheque.event.sourcing.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("fr.fredos.dvdtheque.event.sourcing.demo.events.store")
public class EventSourcingSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventSourcingSpringBootApplication.class, args);
	}
}
