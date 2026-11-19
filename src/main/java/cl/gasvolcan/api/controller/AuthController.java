package cl.gasvolcan.api.controller;

import cl.gasvolcan.api.dto.*;
import cl.gasvolcan.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Login, registro y renovación de tokens JWT")
public class AuthController {

    private final AuthService servicio;

    public AuthController(AuthService servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve un access token (30 min) y un refresh token (7 días).")
    public TokenResponse login(@Valid @RequestBody LoginRequest r) {
        return servicio.login(r);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un cliente", description = "El registro público siempre crea un usuario con rol Cliente.")
    public UsuarioResponse registrar(@Valid @RequestBody UsuarioRequest r) {
        return servicio.registrar(r);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar tokens", description = "Con un refresh token válido entrega un access token y un refresh token nuevos.")
    public TokenResponse renovar(@Valid @RequestBody RefreshRequest r) {
        return servicio.renovar(r.refreshToken());
    }

    @GetMapping("/me")
    @Operation(summary = "Datos del usuario autenticado")
    public UsuarioResponse yo() {
        return servicio.yo();
    }
}
