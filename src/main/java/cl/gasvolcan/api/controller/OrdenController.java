package cl.gasvolcan.api.controller;

import cl.gasvolcan.api.dto.*;
import cl.gasvolcan.api.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
@Tag(name = "Órdenes y reparto", description = "Compras, pedidos, asignación de camión y repartidor, y estados")
public class OrdenController {

    private final OrdenService servicio;

    public OrdenController(OrdenService servicio) {
        this.servicio = servicio;
    }

    // ---------- lectura ----------

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADORA')")
    @Operation(summary = "Listar todas las órdenes (Operadora y Administrador)")
    public List<OrdenResponse> listar() {
        return servicio.listarTodas();
    }

    @GetMapping("/mis")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Mis órdenes (Cliente)")
    public List<OrdenResponse> mias() {
        return servicio.listarMias();
    }

    @GetMapping("/mis-entregas")
    @PreAuthorize("hasRole('REPARTIDOR')")
    @Operation(summary = "Pedidos asignados al repartidor que inició sesión (Repartidor)")
    public List<OrdenResponse> misEntregas() {
        return servicio.listarEntregasDelRepartidor();
    }

    @GetMapping("/{numero}")
    @Operation(summary = "Consultar una orden", description = "Personal de la empresa, el dueño de la orden o su repartidor.")
    public OrdenResponse obtener(@PathVariable String numero) {
        return servicio.obtener(numero);
    }

    // ---------- creación ----------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Comprar el carrito (checkout)",
            description = "Público. Calcula precios en el servidor, procesa el pago simulado y descuenta stock. "
                    + "Si el pago falla la orden queda en estado pago-fallido.")
    public OrdenResponse comprar(@Valid @RequestBody CompraRequest r) {
        return servicio.crearCompra(r);
    }

    @PutMapping("/{numero}/pago")
    @Operation(summary = "Reintentar el pago de una orden con pago fallido")
    public OrdenResponse reintentarPago(@PathVariable String numero, @Valid @RequestBody ReintentoPagoRequest r) {
        return servicio.reintentarPago(numero, r);
    }

    @PostMapping("/rapido")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Pedido rápido de un producto (público, pago en efectivo)")
    public OrdenResponse pedidoRapido(@Valid @RequestBody PedidoRapidoRequest r) {
        return servicio.crearPedidoRapido(r);
    }

    @PostMapping("/telefono")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADORA')")
    @Operation(summary = "Registrar un pedido recibido por llamada (Operadora)")
    public OrdenResponse pedidoTelefonico(@Valid @RequestBody PedidoRapidoRequest r) {
        return servicio.crearPedidoTelefonico(r);
    }

    // ---------- reparto ----------

    @PostMapping("/{numero}/asignacion")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADORA')")
    @Operation(summary = "Asignar camión y repartidor a un pedido pendiente (Operadora)",
            description = "Un repartidor sale en un solo camión y un camión lo lleva un solo repartidor, "
                    + "pero puede llevar varios pedidos. Responde 409 si se rompe esa regla.")
    public OrdenResponse asignar(@PathVariable String numero, @Valid @RequestBody AsignacionRequest r) {
        return servicio.asignar(numero, r);
    }

    @PutMapping("/{numero}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADORA','REPARTIDOR')")
    @Operation(summary = "Cambiar el estado de un pedido",
            description = "en-camino y entregado: el repartidor asignado. cancelado: la operadora. "
                    + "El administrador puede hacer cualquiera. Flujo: pendiente, asignado, en-camino, entregado.")
    public OrdenResponse cambiarEstado(@PathVariable String numero, @Valid @RequestBody CambioEstadoRequest r) {
        return servicio.cambiarEstado(numero, r.estado());
    }
}
