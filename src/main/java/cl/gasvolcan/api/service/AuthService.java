package cl.gasvolcan.api.service;

import cl.gasvolcan.api.dto.LoginRequest;
import cl.gasvolcan.api.dto.TokenResponse;
import cl.gasvolcan.api.dto.UsuarioRequest;
import cl.gasvolcan.api.dto.UsuarioResponse;
import cl.gasvolcan.api.exception.CredencialesInvalidasException;
import cl.gasvolcan.api.model.TipoUsuario;
import cl.gasvolcan.api.model.Usuario;
import cl.gasvolcan.api.repository.UsuarioRepository;
import cl.gasvolcan.api.security.JwtService;
import cl.gasvolcan.api.security.SesionActual;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Login, registro, renovación de tokens y "quién soy". */
@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final UsuarioService usuarioService;
    private final SesionActual sesion;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt,
                       UsuarioService usuarioService, SesionActual sesion) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
        this.usuarioService = usuarioService;
        this.sesion = sesion;
    }

    /** Verifica correo y contraseña (BCrypt) y entrega los tokens. */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest r) {
        Usuario u = usuarios.findByCorreo(r.correo().trim().toLowerCase())
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos"));
        if (!encoder.matches(r.contrasena().trim(), u.getContrasena())) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
        }
        return tokens(u);
    }

    /** El registro público siempre crea un Cliente. */
    @Transactional
    public UsuarioResponse registrar(UsuarioRequest r) {
        return usuarioService.crear(r, TipoUsuario.CLIENTE);
    }

    /** Con un refresh token válido entrega un par de tokens nuevo (renovación de la sesión). */
    @Transactional(readOnly = true)
    public TokenResponse renovar(String refreshToken) {
        String correo = jwt.correoDeRefreshToken(refreshToken);
        Usuario u = usuarios.findByCorreo(correo)
                .orElseThrow(() -> new CredencialesInvalidasException("El usuario ya no existe"));
        return tokens(u);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse yo() {
        Usuario u = usuarios.findByCorreo(sesion.requerir().correo())
                .orElseThrow(() -> new CredencialesInvalidasException("El usuario ya no existe"));
        return UsuarioService.aRespuesta(u);
    }

    private TokenResponse tokens(Usuario u) {
        return new TokenResponse(jwt.generarAccessToken(u), jwt.generarRefreshToken(u), "Bearer",
                UsuarioService.aRespuesta(u));
    }
}
