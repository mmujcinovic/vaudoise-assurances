package io.mmujcinovic.vaudoiseassurances;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Enables automatic auditing (e.g. @CreatedDate, @LastModifiedDate)
public class VaudoiseAssurancesApplication {

	public static void main(String[] args) {
		SpringApplication.run(VaudoiseAssurancesApplication.class, args);
	}
}
