package cl.gasvolcan.api.service;

import cl.gasvolcan.api.dto.ProductoDto;
import cl.gasvolcan.api.exception.RecursoNoEncontradoException;
import cl.gasvolcan.api.exception.ReglaNegocioException;
import cl.gasvolcan.api.exception.SolicitudInvalidaException;
import cl.gasvolcan.api.model.Producto;
import cl.gasvolcan.api.repository.CategoriaRepository;
import cl.gasvolcan.api.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CRUD del catálogo de productos. */
@Service
public class ProductoService {

    private final ProductoRepository productos;
    private final CategoriaRepository categorias;

    public ProductoService(ProductoRepository productos, CategoriaRepository categorias) {
        this.productos = productos;
        this.categorias = categorias;
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> listar() {
        return productos.findAll().stream().map(ProductoService::aDto).toList();
    }

    @Transactional(readOnly = true)
    public ProductoDto obtener(String codigo) {
        return aDto(buscar(codigo));
    }

    @Transactional
    public ProductoDto crear(ProductoDto d) {
        String codigo = d.codigo().trim().toUpperCase();
        if (productos.existsById(codigo)) {
            throw new ReglaNegocioException("Ya existe un producto con el código " + codigo);
        }
        Producto p = new Producto();
        p.setCodigo(codigo);
        copiar(p, d);
        return aDto(productos.save(p));
    }

    @Transactional
    public ProductoDto actualizar(String codigo, ProductoDto d) {
        Producto p = buscar(codigo);
        copiar(p, d);
        return aDto(productos.save(p));
    }

    @Transactional
    public void eliminar(String codigo) {
        productos.delete(buscar(codigo));
    }

    private Producto buscar(String codigo) {
        return productos.findById(codigo.trim().toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto " + codigo));
    }

    private void copiar(Producto p, ProductoDto d) {
        if (!categorias.existsById(d.categoriaId())) {
            throw new SolicitudInvalidaException("La categoría " + d.categoriaId() + " no existe");
        }
        p.setCategoriaId(d.categoriaId());
        p.setNombre(d.nombre().trim());
        p.setDescripcion(d.descripcion() == null ? "" : d.descripcion().trim());
        p.setPrecio(d.precio());
        p.setDescuento(d.descuento() == null ? 0 : d.descuento());
        p.setStock(d.stock());
        p.setStockCritico(d.stockCritico());
        p.setImagen(d.imagen() == null ? "" : d.imagen().trim());
    }

    static ProductoDto aDto(Producto p) {
        return new ProductoDto(p.getCodigo(), p.getCategoriaId(), p.getNombre(), p.getDescripcion(), p.getPrecio(),
                p.getDescuento(), p.getStock(), p.getStockCritico(), p.getImagen());
    }
}
