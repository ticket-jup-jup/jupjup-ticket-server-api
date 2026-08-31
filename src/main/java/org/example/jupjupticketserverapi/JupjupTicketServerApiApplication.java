package org.example.jupjupticketserverapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JupjupTicketServerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JupjupTicketServerApiApplication.class, args);
    }

}
