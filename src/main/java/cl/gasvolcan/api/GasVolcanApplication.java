package cl.gasvolcan.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada del backend. Levanta la API en http://localhost:8080 */
@SpringBootApplication
public class GasVolcanApplication {

    public static void main(String[] args) {
        SpringApplication.run(GasVolcanApplication.class, args);
    }
}
