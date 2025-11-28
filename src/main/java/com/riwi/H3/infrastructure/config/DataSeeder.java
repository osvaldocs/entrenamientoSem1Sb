package com.riwi.H3.infrastructure.config;

import com.riwi.H3.infrastructure.entity.EventEntity;
import com.riwi.H3.infrastructure.entity.VenueEntity;
import com.riwi.H3.infrastructure.repository.jpa.EventJpaRepository;
import com.riwi.H3.infrastructure.repository.jpa.VenueJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initData(EventJpaRepository eventRepo, VenueJpaRepository venueRepo) {
        return args -> {

            // ----- VENUES -----
            VenueEntity stadium = new VenueEntity();
            stadium.setName("City Stadium");
            stadium.setCapacity(50000);
            venueRepo.save(stadium);

            VenueEntity theater = new VenueEntity();
            theater.setName("Grand Theater");
            theater.setCapacity(1200);
            venueRepo.save(theater);

            // ----- EVENTS -----
            EventEntity concert = new EventEntity();
            concert.setName("Rock Festival");
            concert.setDate(LocalDate.now().plusDays(10));
            concert.setVenue(stadium);
            eventRepo.save(concert);

            EventEntity gala = new EventEntity();
            gala.setName("Charity Gala");
            gala.setDate(LocalDate.now().plusDays(20));
            gala.setVenue(theater);
            eventRepo.save(gala);

            // Generar muchos eventos para probar paginación
            for (int i = 1; i <= 50; i++) {
                EventEntity e = new EventEntity();
                e.setName("Evento Test " + i);
                e.setDate(LocalDate.now().plusDays(i));
                e.setVenue(stadium);
                eventRepo.save(e);
            }

            System.out.println(">>> Datos de prueba cargados correctamente <<<");
        };
    }
}
