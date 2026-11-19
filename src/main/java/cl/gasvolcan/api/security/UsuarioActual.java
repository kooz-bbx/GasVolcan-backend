package cl.gasvolcan.api.security;

import cl.gasvolcan.api.model.TipoUsuario;

/** El usuario que hizo la petición (sale del token JWT). Es el "principal" de Spring Security. */
public record UsuarioActual(String run, String correo, String nombre, TipoUsuario tipo) {
}
