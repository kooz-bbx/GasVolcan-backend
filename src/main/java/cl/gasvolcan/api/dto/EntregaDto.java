package cl.gasvolcan.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EntregaDto(
        @NotBlank(message = "El tipo de entrega es obligatorio") String tipo,
        @NotBlank(message = "La dirección de entrega es obligatoria")
        @Size(max = 300, message = "La dirección no puede superar los 300 caracteres") String calle,
        @Size(max = 50) String departamento,
        @Size(max = 100) String region,
        @Size(max = 100) String comuna,
        @Size(max = 500, message = "Las indicaciones no pueden superar los 500 caracteres") String indicaciones) {
}
