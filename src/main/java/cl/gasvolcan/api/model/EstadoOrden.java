package cl.gasvolcan.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

/**
 * Estados de un pedido:
 * pendiente -> asignado (la operadora asigna camión y repartidor) -> en-camino -> entregado.
 * También puede quedar cancelado o con pago-fallido.
 */
public enum EstadoOrden {
    PENDIENTE("pendiente"),
    ASIGNADO("asignado"),
    EN_CAMINO("en-camino"),
    ENTREGADO("entregado"),
    CANCELADO("cancelado"),
    PAGO_FALLIDO("pago-fallido");

    private final String etiqueta;

    EstadoOrden(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @JsonValue
    public String getEtiqueta() {
        return etiqueta;
    }

    @JsonCreator
    public static EstadoOrden desdeTexto(String texto) {
        for (EstadoOrden e : values()) {
            if (e.etiqueta.equalsIgnoreCase(texto) || e.name().equalsIgnoreCase(texto)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado inválido: " + texto);
    }

    /** Estados a los que se puede pasar desde este estado. */
    public List<EstadoOrden> siguientes() {
        return switch (this) {
            case PENDIENTE -> List.of(CANCELADO);
            case ASIGNADO -> List.of(EN_CAMINO, CANCELADO);
            case EN_CAMINO -> List.of(ENTREGADO, CANCELADO);
            default -> List.of();
        };
    }

    /** Un pedido "activo" ya tiene camión y repartidor y todavía no termina. */
    public boolean esActivo() {
        return this == ASIGNADO || this == EN_CAMINO;
    }
}
