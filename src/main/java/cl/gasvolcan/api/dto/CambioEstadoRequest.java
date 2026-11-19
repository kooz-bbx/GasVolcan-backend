package cl.gasvolcan.api.dto;

import cl.gasvolcan.api.model.EstadoOrden;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(@NotNull(message = "El estado es obligatorio") EstadoOrden estado) {
}
