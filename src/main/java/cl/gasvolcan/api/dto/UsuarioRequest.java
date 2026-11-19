package cl.gasvolcan.api.dto;

import cl.gasvolcan.api.model.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear o editar un usuario (registro público o mantenedor del administrador).
 * La contraseña es obligatoria al crear y opcional al editar (si va vacía se conserva la anterior).
 */
public record UsuarioRequest(
        @NotBlank(message = "El RUN es obligatorio")
        @Pattern(regexp = "^\\d{7,8}[0-9kK]$", message = "RUN inválido (sin puntos ni guión, ej: 190110222)")
        String run,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres")
        String apellidos,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Correo con formato inválido")
        @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
        String correo,

        @Size(min = 4, max = 10, message = "La contraseña debe tener entre 4 y 10 caracteres")
        String contrasena,

        @Size(max = 100) String region,
        @Size(max = 100) String comuna,
        @Size(max = 300, message = "La dirección no puede superar los 300 caracteres") String direccion,

        TipoUsuario tipoUsuario) {
}
