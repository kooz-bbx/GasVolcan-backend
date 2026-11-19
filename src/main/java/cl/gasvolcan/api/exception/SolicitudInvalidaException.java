package cl.gasvolcan.api.exception;

/** Responde 400, datos que no cumplen una regla. */
public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
