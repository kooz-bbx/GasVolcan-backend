package cl.gasvolcan.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Pago simulado. El número de tarjeta solo se usa para validar y NUNCA se guarda. */
public record PagoRequest(
        @NotBlank(message = "El método de pago es obligatorio")
        @Pattern(regexp = "efectivo|tarjeta", message = "El método de pago debe ser efectivo o tarjeta")
        String metodo,
        String numeroTarjeta) {
}
