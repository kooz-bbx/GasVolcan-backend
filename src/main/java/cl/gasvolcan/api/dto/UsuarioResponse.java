package cl.gasvolcan.api.dto;

import cl.gasvolcan.api.model.TipoUsuario;

/** Datos públicos de un usuario (nunca incluye la contraseña). */
public record UsuarioResponse(String run, String nombre, String apellidos, String correo,
                              String region, String comuna, String direccion, TipoUsuario tipoUsuario) {
}
