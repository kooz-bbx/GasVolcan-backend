package cl.gasvolcan.api.dto;

import cl.gasvolcan.api.model.EstadoOrden;

import java.time.LocalDateTime;
import java.util.List;

/** Una orden completa: cliente, entrega, pago, productos y datos del reparto. */
public record OrdenResponse(
        String numero,
        LocalDateTime fecha,
        String origen,
        String propietario,
        ClienteDto cliente,
        EntregaDto entrega,
        PagoResponse pago,
        List<ItemResponse> items,
        int total,
        EstadoOrden estado,
        String repartidor,
        String repartidorRun,
        String camion,
        String asignadoPor,
        LocalDateTime fechaAsignacion,
        String instrucciones,
        LocalDateTime fechaEntrega,
        String motivoPago) {
}
