package cl.gasvolcan.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Pedido de un solo producto, pagado en efectivo al recibir.
 * Lo usa el formulario "Pedido Rápido" de la tienda y la operadora cuando anota un pedido por llamada.
 */
public record PedidoRapidoRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String nombre,
        @NotBlank(message = "El teléfono es obligatorio") String telefono,
        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 300, message = "La dirección no puede superar los 300 caracteres") String direccion,
        @Size(max = 100) String comuna,
        @NotBlank(message = "El producto es obligatorio") String codigoProducto,
        @Min(value = 1, message = "La cantidad debe ser al menos 1") int cantidad,
        @Size(max = 500, message = "Las indicaciones no pueden superar los 500 caracteres") String indicaciones) {
}
