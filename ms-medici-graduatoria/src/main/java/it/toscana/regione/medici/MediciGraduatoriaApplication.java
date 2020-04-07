package it.toscana.regione.medici;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "it.toscana.regione.medici" })

public class MediciGraduatoriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediciGraduatoriaApplication.class, args);
	}
	
}
