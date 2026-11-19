package cl.gasvolcan.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** La operadora elige el camión y el repartidor de un pedido y le deja instrucciones. */
public record AsignacionRequest(
        @NotBlank(message = "Selecciona un camión") String camion,
        @NotBlank(message = "Selecciona un repartidor") String repartidorRun,
        @Size(max = 500, message = "Las instrucciones no pueden superar los 500 caracteres") String instrucciones) {
}
