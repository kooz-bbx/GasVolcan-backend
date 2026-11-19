package cl.gasvolcan.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Tabla "producto": los cilindros de gas del catálogo. */
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @Column(length = 20)
    private String codigo;

    @Column(name = "categoria_id", nullable = false, length = 100)
    private String categoriaId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    /** Precio de lista en CLP (sin descuento). */
    @Column(nullable = false)
    private int precio;

    /** Porcentaje de descuento (0 a 90). */
    @Column(nullable = false)
    private int descuento;

    @Column(nullable = false)
    private int stock;

    /** Si el stock baja de este número, el producto aparece como "stock crítico". */
    @Column(name = "stock_critico")
    private Integer stockCritico;

    @Column(length = 300)
    private String imagen;

    /** Precio final con el descuento aplicado. */
    public int precioFinal() {
        return (int) Math.round(precio * (100 - descuento) / 100.0);
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getCategoriaId() { return categoriaId; }
    public void setCategoriaId(String categoriaId) { this.categoriaId = categoriaId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getPrecio() { return precio; }
    public void setPrecio(int precio) { this.precio = precio; }
    public int getDescuento() { return descuento; }
    public void setDescuento(int descuento) { this.descuento = descuento; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public Integer getStockCritico() { return stockCritico; }
    public void setStockCritico(Integer stockCritico) { this.stockCritico = stockCritico; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}
