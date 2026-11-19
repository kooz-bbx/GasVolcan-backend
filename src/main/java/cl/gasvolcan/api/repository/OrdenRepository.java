package cl.gasvolcan.api.repository;

import cl.gasvolcan.api.model.EstadoOrden;
import cl.gasvolcan.api.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, String> {

    List<Orden> findAllByOrderByFechaDesc();

    List<Orden> findByPropietarioOrderByFechaDesc(String propietario);

    List<Orden> findByRepartidorRunOrderByFechaDesc(String repartidorRun);

    List<Orden> findByEstadoIn(Collection<EstadoOrden> estados);
}
