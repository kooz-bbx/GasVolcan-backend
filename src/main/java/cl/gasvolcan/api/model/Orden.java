package cl.gasvolcan.api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Tabla "orden": una compra o pedido. Guarda los datos del cliente y de la entrega,
 * el pago y, cuando la operadora la asigna, el camión y el repartidor.
 */
@Entity
@Table(name = "orden")
public class Orden {

    @Id
    @Column(length = 20)
    private String numero;

    @Column(nullable = false)
    private LocalDateTime fecha;

    /** checkout (tienda), pedido-rapido (formulario web) o telefono (operadora). */
    @Column(nullable = false, length = 20)
    private String origen;

    /** Correo del usuario que compró, "invitado" si no tenía sesión o "telefono". */
    @Column(nullable = false, length = 100)
    private String propietario;

    // --- cliente ---
    @Column(name = "cliente_nombre", nullable = false, length = 100)
    private String clienteNombre;
    @Column(name = "cliente_apellidos", length = 100)
    private String clienteApellidos;
    @Column(name = "cliente_correo", length = 100)
    private String clienteCorreo;
    @Column(name = "cliente_telefono", length = 20)
    private String clienteTelefono;

    // --- entrega ---
    @Column(name = "entrega_tipo", nullable = false, length = 20)
    private String entregaTipo;
    @Column(name = "entrega_calle", nullable = false, length = 300)
    private String entregaCalle;
    @Column(name = "entrega_departamento", length = 50)
    private String entregaDepartamento;
    @Column(name = "entrega_region", length = 100)
    private String entregaRegion;
    @Column(name = "entrega_comuna", length = 100)
    private String entregaComuna;
    @Column(name = "entrega_indicaciones", length = 500)
    private String entregaIndicaciones;

    // --- pago ---
    @Column(name = "pago_metodo", nullable = false, length = 20)
    private String pagoMetodo;
    @Column(name = "pago_pagado", nullable = false)
    private boolean pagado;

    @Column(nullable = false)
    private int total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado;

    // --- reparto (los completa la operadora) ---
    @Column(length = 10)
    private String camion;
    @Column(name = "repartidor_run", length = 9)
    private String repartidorRun;
    @Column(length = 150)
    private String repartidor = "Sin asignar";
    @Column(name = "asignado_por", length = 100)
    private String asignadoPor;
    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;
    @Column(length = 500)
    private String instrucciones;
    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrdenItem> items = new ArrayList<>();

    public void agregarItem(Producto p, int cantidad) {
        OrdenItem item = new OrdenItem();
        item.setOrden(this);
        item.setCodigo(p.getCodigo());
        item.setNombre(p.getNombre());
        item.setImagen(p.getImagen());
        item.setPrecio(p.precioFinal());
        item.setCantidad(cantidad);
        items.add(item);
    }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    public String getPropietario() { return propietario; }
    public void setPropietario(String propietario) { this.propietario = propietario; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String v) { this.clienteNombre = v; }
    public String getClienteApellidos() { return clienteApellidos; }
    public void setClienteApellidos(String v) { this.clienteApellidos = v; }
    public String getClienteCorreo() { return clienteCorreo; }
    public void setClienteCorreo(String v) { this.clienteCorreo = v; }
    public String getClienteTelefono() { return clienteTelefono; }
    public void setClienteTelefono(String v) { this.clienteTelefono = v; }
    public String getEntregaTipo() { return entregaTipo; }
    public void setEntregaTipo(String v) { this.entregaTipo = v; }
    public String getEntregaCalle() { return entregaCalle; }
    public void setEntregaCalle(String v) { this.entregaCalle = v; }
    public String getEntregaDepartamento() { return entregaDepartamento; }
    public void setEntregaDepartamento(String v) { this.entregaDepartamento = v; }
    public String getEntregaRegion() { return entregaRegion; }
    public void setEntregaRegion(String v) { this.entregaRegion = v; }
    public String getEntregaComuna() { return entregaComuna; }
    public void setEntregaComuna(String v) { this.entregaComuna = v; }
    public String getEntregaIndicaciones() { return entregaIndicaciones; }
    public void setEntregaIndicaciones(String v) { this.entregaIndicaciones = v; }
    public String getPagoMetodo() { return pagoMetodo; }
    public void setPagoMetodo(String v) { this.pagoMetodo = v; }
    public boolean isPagado() { return pagado; }
    public void setPagado(boolean pagado) { this.pagado = pagado; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }
    public String getCamion() { return camion; }
    public void setCamion(String camion) { this.camion = camion; }
    public String getRepartidorRun() { return repartidorRun; }
    public void setRepartidorRun(String v) { this.repartidorRun = v; }
    public String getRepartidor() { return repartidor; }
    public void setRepartidor(String repartidor) { this.repartidor = repartidor; }
    public String getAsignadoPor() { return asignadoPor; }
    public void setAsignadoPor(String v) { this.asignadoPor = v; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDateTime v) { this.fechaAsignacion = v; }
    public String getInstrucciones() { return instrucciones; }
    public void setInstrucciones(String v) { this.instrucciones = v; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime v) { this.fechaEntrega = v; }
    public List<OrdenItem> getItems() { return items; }
}
