package cl.gasvolcan.api.controller;

import cl.gasvolcan.api.dto.CategoriaRequest;
import cl.gasvolcan.api.dto.CategoriaResponse;
import cl.gasvolcan.api.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorías", description = "Lectura pública; crear, editar y eliminar solo Administrador")
public class CategoriaController {

    private final CategoriaService servicio;

    public CategoriaController(CategoriaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Listar categorías (público)")
    public List<CategoriaResponse> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una categoría (público)")
    public CategoriaResponse obtener(@PathVariable String id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear categoría (Administrador)")
    public CategoriaResponse crear(@Valid @RequestBody CategoriaRequest r) {
        return servicio.crear(r);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Editar categoría (Administrador)")
    public CategoriaResponse actualizar(@PathVariable String id, @Valid @RequestBody CategoriaRequest r) {
        return servicio.actualizar(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar categoría (Administrador)", description = "Responde 409 si la categoría tiene productos.")
    public void eliminar(@PathVariable String id) {
        servicio.eliminar(id);
    }
}
