package cl.gasvolcan.api.controller;

import cl.gasvolcan.api.dto.UsuarioRequest;
import cl.gasvolcan.api.dto.UsuarioResponse;
import cl.gasvolcan.api.model.TipoUsuario;
import cl.gasvolcan.api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Mantenedor de usuarios (solo Administrador) y listado de repartidores")
public class UsuarioController {

    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar usuarios (Administrador)")
    public List<UsuarioResponse> listar() {
        return servicio.listar();
    }

    @GetMapping("/repartidores")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','OPERADORA')")
    @Operation(summary = "Listar repartidores (Operadora y Administrador)")
    public List<UsuarioResponse> repartidores() {
        return servicio.listarRepartidores();
    }

    @GetMapping("/{run}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Consultar un usuario por RUN (Administrador)")
    public UsuarioResponse obtener(@PathVariable String run) {
        return servicio.obtener(run);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear usuario con cualquier rol (Administrador)")
    public UsuarioResponse crear(@Valid @RequestBody UsuarioRequest r) {
        return servicio.crear(r, (TipoUsuario) null);
    }

    @PutMapping("/{run}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Editar usuario (Administrador)")
    public UsuarioResponse actualizar(@PathVariable String run, @Valid @RequestBody UsuarioRequest r) {
        return servicio.actualizar(run, r);
    }

    @DeleteMapping("/{run}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar usuario (Administrador)")
    public void eliminar(@PathVariable String run) {
        servicio.eliminar(run);
    }
}
