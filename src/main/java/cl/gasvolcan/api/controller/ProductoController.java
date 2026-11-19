package cl.gasvolcan.api.controller;

import cl.gasvolcan.api.dto.ProductoDto;
import cl.gasvolcan.api.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Catálogo: lectura pública; crear, editar y eliminar solo Administrador")
public class ProductoController {

    private final ProductoService servicio;

    public ProductoController(ProductoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Listar productos (público)")
    public List<ProductoDto> listar() {
        return servicio.listar();
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Consultar un producto (público)")
    public ProductoDto obtener(@PathVariable String codigo) {
        return servicio.obtener(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear producto (Administrador)")
    public ProductoDto crear(@Valid @RequestBody ProductoDto d) {
        return servicio.crear(d);
    }

    @PutMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Editar producto (Administrador)")
    public ProductoDto actualizar(@PathVariable String codigo, @Valid @RequestBody ProductoDto d) {
        return servicio.actualizar(codigo, d);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar producto (Administrador)")
    public void eliminar(@PathVariable String codigo) {
        servicio.eliminar(codigo);
    }
}
