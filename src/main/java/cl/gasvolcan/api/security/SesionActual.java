package cl.gasvolcan.api.security;

import cl.gasvolcan.api.exception.CredencialesInvalidasException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Atajo para que los servicios sepan quién hizo la petición. */
@Component
public class SesionActual {

    /** El usuario autenticado, o vacío si la petición es anónima (invitado). */
    public Optional<UsuarioActual> opcional() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a != null && a.getPrincipal() instanceof UsuarioActual u) {
            return Optional.of(u);
        }
        return Optional.empty();
    }

    public UsuarioActual requerir() {
        return opcional().orElseThrow(() -> new CredencialesInvalidasException("Debes iniciar sesión"));
    }
}
