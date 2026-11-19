package cl.gasvolcan.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** Un producto del carrito: solo viaja el código y la cantidad (el precio lo calcula el backend). */
public record ItemRequest(
        @NotBlank(message = "El código del producto es obligatorio") String codigo,
        @Min(value = 1, message = "La cantidad debe ser al menos 1") int cantidad) {
}
