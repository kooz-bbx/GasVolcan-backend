package cl.gasvolcan.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Compra del carrito (checkout). */
public record CompraRequest(
        @NotNull(message = "Faltan los datos del cliente") @Valid ClienteDto cliente,
        @NotNull(message = "Faltan los datos de entrega") @Valid EntregaDto entrega,
        @NotNull(message = "Falta el método de pago") @Valid PagoRequest pago,
        @NotEmpty(message = "El carrito está vacío") List<@Valid ItemRequest> items) {
}
