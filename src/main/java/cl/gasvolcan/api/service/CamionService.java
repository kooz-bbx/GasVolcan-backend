package cl.gasvolcan.api.service;

import cl.gasvolcan.api.dto.CamionDto;
import cl.gasvolcan.api.repository.CamionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Consulta de la flota de camiones. */
@Service
public class CamionService {

    private final CamionRepository camiones;

    public CamionService(CamionRepository camiones) {
        this.camiones = camiones;
    }

    @Transactional(readOnly = true)
    public List<CamionDto> listar() {
        return camiones.findAll().stream()
                .map(c -> new CamionDto(c.getPatente(), c.getModelo(), c.getCapacidad()))
                .toList();
    }
}
