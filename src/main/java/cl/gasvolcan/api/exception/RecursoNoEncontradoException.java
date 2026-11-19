package cl.gasvolcan.api.exception;

/** Responde 404, no existe lo pedido. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
