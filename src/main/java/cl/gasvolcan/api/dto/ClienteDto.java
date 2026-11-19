package cl.gasvolcan.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteDto(
        @NotBlank(message = "El nombre del cliente es obligatorio") @Size(max = 100) String nombre,
        @Size(max = 100) String apellidos,
        @Size(max = 100) String correo,
        @Size(max = 20) String telefono) {
}
