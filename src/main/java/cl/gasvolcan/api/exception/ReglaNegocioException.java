package cl.gasvolcan.api.exception;

/** Responde 409, rompe una regla del negocio (duplicados, stock, estados, etc.). */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
