package cl.gasvolcan.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** Datos para volver a pagar una orden cuyo pago falló. */
public record ReintentoPagoRequest(
        @NotNull(message = "Faltan los datos del cliente") @Valid ClienteDto cliente,
        @NotNull(message = "Faltan los datos de entrega") @Valid EntregaDto entrega,
        @NotNull(message = "Falta el método de pago") @Valid PagoRequest pago) {
}
