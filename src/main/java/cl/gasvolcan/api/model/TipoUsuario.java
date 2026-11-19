package cl.gasvolcan.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Los 4 perfiles del caso. En el JSON viajan con su nombre en español
 * ("Cliente", "Administrador", "Operadora", "Repartidor").
 */
public enum TipoUsuario {
    CLIENTE("Cliente"),
    ADMINISTRADOR("Administrador"),
    OPERADORA("Operadora"),
    REPARTIDOR("Repartidor");

    private final String etiqueta;

    TipoUsuario(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @JsonValue
    public String getEtiqueta() {
        return etiqueta;
    }

    @JsonCreator
    public static TipoUsuario desdeTexto(String texto) {
        for (TipoUsuario t : values()) {
            if (t.etiqueta.equalsIgnoreCase(texto) || t.name().equalsIgnoreCase(texto)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de usuario inválido: " + texto);
    }
}
