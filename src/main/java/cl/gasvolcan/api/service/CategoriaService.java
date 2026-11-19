package cl.gasvolcan.api.service;

import cl.gasvolcan.api.dto.CategoriaRequest;
import cl.gasvolcan.api.dto.CategoriaResponse;
import cl.gasvolcan.api.exception.RecursoNoEncontradoException;
import cl.gasvolcan.api.exception.ReglaNegocioException;
import cl.gasvolcan.api.model.Categoria;
import cl.gasvolcan.api.repository.CategoriaRepository;
import cl.gasvolcan.api.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;

/** CRUD de categorías. Regla: no se puede eliminar una categoría que todavía tiene productos. */
@Service
public class CategoriaService {

    private final CategoriaRepository categorias;
    private final ProductoRepository productos;

    public CategoriaService(CategoriaRepository categorias, ProductoRepository productos) {
        this.categorias = categorias;
        this.productos = productos;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categorias.findAll().stream().map(CategoriaService::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(String id) {
        return aRespuesta(buscar(id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest r) {
        Categoria c = new Categoria(generarId(r.nombre()), r.nombre().trim(),
                r.descripcion() == null ? "" : r.descripcion().trim());
        return aRespuesta(categorias.save(c));
    }

    @Transactional
    public CategoriaResponse actualizar(String id, CategoriaRequest r) {
        Categoria c = buscar(id);
        c.setNombre(r.nombre().trim());
        c.setDescripcion(r.descripcion() == null ? "" : r.descripcion().trim());
        return aRespuesta(categorias.save(c));
    }

    @Transactional
    public void eliminar(String id) {
        Categoria c = buscar(id);
        if (productos.existsByCategoriaId(id)) {
            throw new ReglaNegocioException("La categoría tiene productos asociados");
        }
        categorias.delete(c);
    }

    private Categoria buscar(String id) {
        return categorias.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoría " + id));
    }

    /** "Uso Comercial" -> "uso-comercial" (si ya existe, agrega -2, -3...). */
    private String generarId(String nombre) {
        String base = Normalizer.normalize(nombre, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (base.isEmpty()) {
            base = "categoria";
        }
        String id = base;
        int n = 2;
        while (categorias.existsById(id)) {
            id = base + "-" + n++;
        }
        return id;
    }

    private static CategoriaResponse aRespuesta(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion());
    }
}
