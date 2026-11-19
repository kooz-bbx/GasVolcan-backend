package cl.gasvolcan.api.repository;

import cl.gasvolcan.api.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, String> {

    boolean existsByCategoriaId(String categoriaId);
}
