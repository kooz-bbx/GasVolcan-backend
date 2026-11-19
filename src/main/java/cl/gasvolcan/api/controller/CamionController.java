package cl.gasvolcan.api.controller;

import cl.gasvolcan.api.dto.CamionDto;
import cl.gasvolcan.api.service.CamionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/camiones")
@Tag(name = "Camiones", description = "Flota de reparto")
public class CamionController {

    private final CamionService servicio;

    public CamionController(CamionService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADORA')")
    @Operation(summary = "Listar camiones (Operadora y Administrador)")
    public List<CamionDto> listar() {
        return servicio.listar();
    }
}
