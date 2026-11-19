package cl.gasvolcan.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Producto del catálogo: sirve para recibir (POST/PUT) y para responder (GET). */
public record ProductoDto(
        @NotBlank(message = "El código es obligatorio")
        @Size(min = 3, max = 20, message = "El código debe tener entre 3 y 20 caracteres")
        String codigo,

        @NotBlank(message = "La categoría es obligatoria")
        String categoriaId,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @Min(value = 0, message = "El precio no puede ser negativo")
        Integer precio,

        @Min(value = 0, message = "El descuento no puede ser negativo")
        @Max(value = 90, message = "El descuento no puede superar el 90%")
        Integer descuento,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        @Min(value = 0, message = "El stock crítico no puede ser negativo")
        Integer stockCritico,

        @Size(max = 300) String imagen) {
}
