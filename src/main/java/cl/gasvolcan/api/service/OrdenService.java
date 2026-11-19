package cl.gasvolcan.api.service;

import cl.gasvolcan.api.dto.*;
import cl.gasvolcan.api.exception.RecursoNoEncontradoException;
import cl.gasvolcan.api.exception.ReglaNegocioException;
import cl.gasvolcan.api.exception.SolicitudInvalidaException;
import cl.gasvolcan.api.model.*;
import cl.gasvolcan.api.repository.*;
import cl.gasvolcan.api.security.SesionActual;
import cl.gasvolcan.api.security.UsuarioActual;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Lógica de negocio de las órdenes:
 *  - compra del carrito (con pago simulado y descuento de stock)
 *  - pedido rápido (web) y pedido telefónico (operadora)
 *  - asignación de camión y repartidor (operadora)
 *  - cambios de estado según el rol (repartidor / operadora / administrador)
 */
@Service
public class OrdenService {

    private final OrdenRepository ordenes;
    private final ProductoRepository productos;
    private final CamionRepository camiones;
    private final UsuarioRepository usuarios;
    private final SesionActual sesion;

    public OrdenService(OrdenRepository ordenes, ProductoRepository productos, CamionRepository camiones,
                        UsuarioRepository usuarios, SesionActual sesion) {
        this.ordenes = ordenes;
        this.productos = productos;
        this.camiones = camiones;
        this.usuarios = usuarios;
        this.sesion = sesion;
    }

    // =====================================================================
    // Consultas
    // =====================================================================

    /** Todas las órdenes (operadora y administrador). */
    @Transactional(readOnly = true)
    public List<OrdenResponse> listarTodas() {
        return ordenes.findAllByOrderByFechaDesc().stream().map(o -> aRespuesta(o, null)).toList();
    }

    /** Las órdenes del usuario que inició sesión (seguimiento del cliente). */
    @Transactional(readOnly = true)
    public List<OrdenResponse> listarMias() {
        String correo = sesion.requerir().correo();
        return ordenes.findByPropietarioOrderByFechaDesc(correo).stream().map(o -> aRespuesta(o, null)).toList();
    }

    /** Las órdenes asignadas al repartidor que inició sesión. */
    @Transactional(readOnly = true)
    public List<OrdenResponse> listarEntregasDelRepartidor() {
        String run = sesion.requerir().run();
        return ordenes.findByRepartidorRunOrderByFechaDesc(run).stream().map(o -> aRespuesta(o, null)).toList();
    }

    /** Una orden, solo si el usuario tiene relación con ella (o es personal de la empresa). */
    @Transactional(readOnly = true)
    public OrdenResponse obtener(String numero) {
        Orden o = buscar(numero);
        UsuarioActual u = sesion.requerir();
        boolean personal = u.tipo() == TipoUsuario.ADMINISTRADOR || u.tipo() == TipoUsuario.OPERADORA;
        boolean esDueno = u.correo().equals(o.getPropietario());
        boolean esRepartidor = u.run().equals(o.getRepartidorRun());
        if (!personal && !esDueno && !esRepartidor) {
            throw new AccessDeniedException("Sin acceso a esta orden");
        }
        return aRespuesta(o, null);
    }

    // =====================================================================
    // Creación de órdenes
    // =====================================================================

    /** Compra del carrito: calcula precios en el servidor, procesa el pago simulado y descuenta stock. */
    @Transactional
    public OrdenResponse crearCompra(CompraRequest r) {
        Orden o = nuevaOrden("checkout", propietarioActual());
        copiarClienteYEntrega(o, r.cliente(), r.entrega());
        cargarItems(o, r.items());
        o.setPagoMetodo(r.pago().metodo());
        return procesarYGuardar(o, r.pago());
    }

    /** Vuelve a intentar el pago de una orden que quedó en "pago-fallido". */
    @Transactional
    public OrdenResponse reintentarPago(String numero, ReintentoPagoRequest r) {
        Orden o = buscar(numero);
        if (!"invitado".equals(o.getPropietario())) {
            // la orden es de un usuario con cuenta: solo ese usuario puede reintentar
            UsuarioActual u = sesion.opcional()
                    .orElseThrow(() -> new AccessDeniedException("Debes iniciar sesión para pagar esta orden"));
            if (!u.correo().equals(o.getPropietario())) {
                throw new AccessDeniedException("Esta orden es de otro usuario");
            }
        }
        if (o.getEstado() != EstadoOrden.PAGO_FALLIDO) {
            throw new ReglaNegocioException("Solo se puede reintentar el pago de órdenes con pago fallido");
        }
        copiarClienteYEntrega(o, r.cliente(), r.entrega());
        o.setPagoMetodo(r.pago().metodo());
        return procesarYGuardar(o, r.pago());
    }

    /** Pedido de un solo producto desde el formulario "Pedido Rápido" (cualquier persona, pago en efectivo). */
    @Transactional
    public OrdenResponse crearPedidoRapido(PedidoRapidoRequest r) {
        return crearPedidoSimple(r, "pedido-rapido", propietarioActual());
    }

    /** Pedido anotado por la operadora durante una llamada telefónica. */
    @Transactional
    public OrdenResponse crearPedidoTelefonico(PedidoRapidoRequest r) {
        return crearPedidoSimple(r, "telefono", "telefono");
    }

    private OrdenResponse crearPedidoSimple(PedidoRapidoRequest r, String origen, String propietario) {
        Producto p = productos.findById(r.codigoProducto().trim().toUpperCase())
                .orElseThrow(() -> new SolicitudInvalidaException("El producto " + r.codigoProducto() + " no existe"));
        if (r.cantidad() > p.getStock()) {
            throw new ReglaNegocioException("Stock insuficiente de " + p.getNombre() + " (quedan " + p.getStock() + ")");
        }
        Orden o = nuevaOrden(origen, propietario);
        o.setClienteNombre(r.nombre().trim());
        o.setClienteApellidos("");
        o.setClienteCorreo("");
        o.setClienteTelefono(normalizarTelefono(r.telefono()));
        o.setEntregaTipo("domicilio");
        o.setEntregaCalle(r.direccion().trim());
        o.setEntregaDepartamento("");
        o.setEntregaRegion("");
        o.setEntregaComuna(r.comuna() == null ? "" : r.comuna().trim());
        o.setEntregaIndicaciones(r.indicaciones() == null ? "" : r.indicaciones().trim());
        o.setPagoMetodo("efectivo");
        o.setPagado(false);
        o.agregarItem(p, r.cantidad());
        o.setTotal(p.precioFinal() * r.cantidad());
        o.setEstado(EstadoOrden.PENDIENTE);
        p.setStock(p.getStock() - r.cantidad());
        productos.save(p);
        return aRespuesta(ordenes.save(o), null);
    }

    // =====================================================================
    // Reparto
    // =====================================================================

    /**
     * La operadora asigna camión y repartidor a un pedido pendiente.
     * Reglas: un repartidor sale en un solo camión y un camión lo lleva un solo repartidor,
     * pero ese repartidor puede llevar varios pedidos a la vez.
     */
    @Transactional
    public OrdenResponse asignar(String numero, AsignacionRequest r) {
        Orden o = buscar(numero);
        if (o.getEstado() != EstadoOrden.PENDIENTE) {
            throw new ReglaNegocioException("Solo se pueden asignar pedidos pendientes");
        }
        Camion camion = camiones.findById(r.camion().trim())
                .orElseThrow(() -> new SolicitudInvalidaException("El camión " + r.camion() + " no existe"));
        Usuario repartidor = usuarios.findById(r.repartidorRun().trim().toUpperCase())
                .filter(u -> u.getTipoUsuario() == TipoUsuario.REPARTIDOR)
                .orElseThrow(() -> new SolicitudInvalidaException("El repartidor seleccionado no existe"));

        List<Orden> activos = ordenes.findByEstadoIn(List.of(EstadoOrden.ASIGNADO, EstadoOrden.EN_CAMINO));
        for (Orden a : activos) {
            if (camion.getPatente().equals(a.getCamion()) && !repartidor.getRun().equals(a.getRepartidorRun())) {
                throw new ReglaNegocioException("El camión " + camion.getPatente() + " ya va en ruta con otro repartidor");
            }
            if (repartidor.getRun().equals(a.getRepartidorRun()) && !camion.getPatente().equals(a.getCamion())) {
                throw new ReglaNegocioException(repartidor.getNombre() + " ya va en ruta en el camión " + a.getCamion());
            }
        }

        o.setEstado(EstadoOrden.ASIGNADO);
        o.setCamion(camion.getPatente());
        o.setRepartidorRun(repartidor.getRun());
        o.setRepartidor(repartidor.getNombre() + " " + repartidor.getApellidos());
        o.setAsignadoPor(sesion.requerir().nombre());
        o.setFechaAsignacion(LocalDateTime.now());
        o.setInstrucciones(r.instrucciones() == null ? "" : r.instrucciones().trim());
        return aRespuesta(ordenes.save(o), null);
    }

    /**
     * Cambia el estado respetando el flujo y el rol:
     *  - en-camino / entregado: el repartidor asignado (o el administrador)
     *  - cancelado: la operadora o el administrador
     * Al entregar se registra la fecha y el pago queda recibido (en efectivo se cobra en ese momento).
     */
    @Transactional
    public OrdenResponse cambiarEstado(String numero, EstadoOrden nuevo) {
        Orden o = buscar(numero);
        if (!o.getEstado().siguientes().contains(nuevo)) {
            throw new ReglaNegocioException("No se puede pasar de \"" + o.getEstado().getEtiqueta()
                    + "\" a \"" + nuevo.getEtiqueta() + "\"");
        }
        UsuarioActual u = sesion.requerir();
        boolean admin = u.tipo() == TipoUsuario.ADMINISTRADOR;
        if (nuevo == EstadoOrden.CANCELADO) {
            if (!admin && u.tipo() != TipoUsuario.OPERADORA) {
                throw new AccessDeniedException("Solo la operadora o el administrador pueden cancelar pedidos");
            }
        } else {
            boolean esSuRepartidor = u.tipo() == TipoUsuario.REPARTIDOR && u.run().equals(o.getRepartidorRun());
            if (!admin && !esSuRepartidor) {
                throw new AccessDeniedException("Solo el repartidor asignado puede actualizar este pedido");
            }
        }
        o.setEstado(nuevo);
        if (nuevo == EstadoOrden.ENTREGADO) {
            o.setFechaEntrega(LocalDateTime.now());
            o.setPagado(true);
        }
        return aRespuesta(ordenes.save(o), null);
    }

    // =====================================================================
    // Privados
    // =====================================================================

    private Orden buscar(String numero) {
        return ordenes.findById(numero.trim().toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la orden " + numero));
    }

    private String propietarioActual() {
        return sesion.opcional().map(UsuarioActual::correo).orElse("invitado");
    }

    private Orden nuevaOrden(String origen, String propietario) {
        Orden o = new Orden();
        o.setNumero(generarNumero());
        o.setFecha(LocalDateTime.now());
        o.setOrigen(origen);
        o.setPropietario(propietario);
        o.setRepartidor("Sin asignar");
        return o;
    }

    /** GV-1234; se repite hasta que no exista. */
    private String generarNumero() {
        String numero;
        do {
            numero = "GV-" + ThreadLocalRandom.current().nextInt(1000, 10000);
        } while (ordenes.existsById(numero));
        return numero;
    }

    private void copiarClienteYEntrega(Orden o, ClienteDto c, EntregaDto e) {
        o.setClienteNombre(c.nombre().trim());
        o.setClienteApellidos(c.apellidos() == null ? "" : c.apellidos().trim());
        o.setClienteCorreo(c.correo() == null ? "" : c.correo().trim().toLowerCase());
        o.setClienteTelefono(c.telefono() == null ? "" : c.telefono().trim());
        o.setEntregaTipo(e.tipo());
        o.setEntregaCalle(e.calle().trim());
        o.setEntregaDepartamento(e.departamento() == null ? "" : e.departamento().trim());
        o.setEntregaRegion(e.region() == null ? "" : e.region());
        o.setEntregaComuna(e.comuna() == null ? "" : e.comuna());
        o.setEntregaIndicaciones(e.indicaciones() == null ? "" : e.indicaciones().trim());
    }

    /** Arma los items con el precio actual del catálogo (el cliente no puede mandar su propio precio). */
    private void cargarItems(Orden o, List<ItemRequest> items) {
        Map<String, Integer> cantidades = new LinkedHashMap<>();
        for (ItemRequest i : items) {
            cantidades.merge(i.codigo().trim().toUpperCase(), i.cantidad(), Integer::sum);
        }
        int total = 0;
        for (Map.Entry<String, Integer> e : cantidades.entrySet()) {
            Producto p = productos.findById(e.getKey())
                    .orElseThrow(() -> new SolicitudInvalidaException("El producto " + e.getKey() + " no existe"));
            o.agregarItem(p, e.getValue());
            total += p.precioFinal() * e.getValue();
        }
        o.setTotal(total);
    }

    /**
     * Pago simulado: efectivo siempre se aprueba (se cobra al entregar);
     * tarjeta debe tener 16 dígitos y si termina en 0000 el banco la rechaza.
     * Si se aprueba, la orden queda "pendiente" y se descuenta el stock; si no, "pago-fallido".
     */
    private OrdenResponse procesarYGuardar(Orden o, PagoRequest pago) {
        String motivo = null;
        if ("tarjeta".equals(pago.metodo())) {
            String numero = pago.numeroTarjeta() == null ? "" : pago.numeroTarjeta().replaceAll("\\s", "");
            if (!numero.matches("\\d{16}")) {
                motivo = "Número de tarjeta inválido.";
            } else if (numero.endsWith("0000")) {
                motivo = "Tarjeta rechazada por el banco (simulación).";
            }
        }
        if (motivo == null) {
            descontarStock(o);
            o.setEstado(EstadoOrden.PENDIENTE);
            o.setPagado("tarjeta".equals(pago.metodo()));
        } else {
            o.setEstado(EstadoOrden.PAGO_FALLIDO);
            o.setPagado(false);
        }
        return aRespuesta(ordenes.save(o), motivo);
    }

    private void descontarStock(Orden o) {
        for (OrdenItem item : o.getItems()) {
            Producto p = productos.findById(item.getCodigo()).orElse(null);
            if (p == null) {
                continue;
            }
            if (item.getCantidad() > p.getStock()) {
                throw new ReglaNegocioException("Stock insuficiente de " + p.getNombre() + " (quedan " + p.getStock() + ")");
            }
            p.setStock(p.getStock() - item.getCantidad());
            productos.save(p);
        }
    }

    /** "+56 9 1234 5678" o "912345678" -> "+56912345678". */
    private String normalizarTelefono(String telefono) {
        String digitos = telefono.replaceAll("[\\s+-]", "");
        if (digitos.startsWith("56") && digitos.length() == 11) {
            digitos = digitos.substring(2);
        }
        if (!digitos.matches("9\\d{8}")) {
            throw new SolicitudInvalidaException("Teléfono inválido: usa el formato +56 9 XXXX XXXX");
        }
        return "+56" + digitos;
    }

    private OrdenResponse aRespuesta(Orden o, String motivoPago) {
        List<ItemResponse> items = o.getItems().stream()
                .map(i -> new ItemResponse(i.getCodigo(), i.getNombre(), i.getImagen(), i.getPrecio(), i.getCantidad()))
                .toList();
        return new OrdenResponse(
                o.getNumero(), o.getFecha(), o.getOrigen(), o.getPropietario(),
                new ClienteDto(o.getClienteNombre(), o.getClienteApellidos(), o.getClienteCorreo(), o.getClienteTelefono()),
                new EntregaDto(o.getEntregaTipo(), o.getEntregaCalle(), o.getEntregaDepartamento(),
                        o.getEntregaRegion(), o.getEntregaComuna(), o.getEntregaIndicaciones()),
                new PagoResponse(o.getPagoMetodo(), o.isPagado()),
                items, o.getTotal(), o.getEstado(), o.getRepartidor(), o.getRepartidorRun(), o.getCamion(),
                o.getAsignadoPor(), o.getFechaAsignacion(), o.getInstrucciones(), o.getFechaEntrega(), motivoPago);
    }
}
