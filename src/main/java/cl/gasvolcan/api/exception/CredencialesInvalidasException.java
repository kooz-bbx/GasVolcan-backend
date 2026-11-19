package cl.gasvolcan.api.exception;

/** Responde 401, correo o contraseña / token incorrectos. */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
