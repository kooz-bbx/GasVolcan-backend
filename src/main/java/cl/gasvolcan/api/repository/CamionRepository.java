package cl.gasvolcan.api.repository;

import cl.gasvolcan.api.model.Camion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CamionRepository extends JpaRepository<Camion, String> {
}
