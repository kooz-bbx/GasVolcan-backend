package cl.gasvolcan.api.security;

import cl.gasvolcan.api.exception.CredencialesInvalidasException;
import cl.gasvolcan.api.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Se ejecuta en cada petición: si trae "Authorization: Bearer <token>" y el token es válido,
 * busca al usuario y lo deja autenticado con su rol. Si no hay token o es inválido, la petición
 * sigue sin autenticar (y Spring Security responde 401 si el recurso es protegido).
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarios;

    public JwtAuthFilter(JwtService jwtService, UsuarioRepository usuarios) {
        this.jwtService = jwtService;
        this.usuarios = usuarios;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String cabecera = request.getHeader("Authorization");
        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            try {
                String correo = jwtService.correoDeAccessToken(cabecera.substring(7));
                // Se consulta el usuario en cada petición: si cambió de rol o lo borraron, se nota al tiro.
                usuarios.findByCorreo(correo).ifPresent(u -> {
                    UsuarioActual actual = new UsuarioActual(u.getRun(), u.getCorreo(), u.getNombre(), u.getTipoUsuario());
                    var autenticacion = new UsernamePasswordAuthenticationToken(
                            actual, null, List.of(new SimpleGrantedAuthority("ROLE_" + u.getTipoUsuario().name())));
                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                });
            } catch (CredencialesInvalidasException e) {
                SecurityContextHolder.clearContext(); // token malo: se trata como anónimo
            }
        }
        chain.doFilter(request, response);
    }
}
