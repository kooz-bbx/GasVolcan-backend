package cl.gasvolcan.api.dto;

/** Respuesta de login y de renovación: los dos tokens JWT y los datos del usuario. */
public record TokenResponse(String accessToken, String refreshToken, String tipo, UsuarioResponse usuario) {
}
