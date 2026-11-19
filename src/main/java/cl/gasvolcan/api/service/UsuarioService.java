package cl.gasvolcan.api.service;

import cl.gasvolcan.api.dto.UsuarioRequest;
import cl.gasvolcan.api.dto.UsuarioResponse;
import cl.gasvolcan.api.exception.RecursoNoEncontradoException;
import cl.gasvolcan.api.exception.ReglaNegocioException;
import cl.gasvolcan.api.exception.SolicitudInvalidaException;
import cl.gasvolcan.api.model.TipoUsuario;
import cl.gasvolcan.api.model.Usuario;
import cl.gasvolcan.api.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CRUD de usuarios y sus reglas: RUN y correo únicos, dominios de correo permitidos, contraseña encriptada. */
@Service
public class UsuarioService {

    /** Dominios de correo aceptados (regla de negocio del proyecto). */
    private static final List<String> DOMINIOS_PERMITIDOS = List.of("duoc.cl", "profesor.duoc.cl", "gmail.com");

    private final UsuarioRepository repositorio;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repositorio, PasswordEncoder encoder) {
        this.repositorio = repositorio;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repositorio.findAll().stream().map(UsuarioService::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarRepartidores() {
        return repositorio.findByTipoUsuario(TipoUsuario.REPARTIDOR).stream().map(UsuarioService::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(String run) {
        return aRespuesta(buscar(run));
    }

    /** Crea un usuario. Si "forzarTipo" viene (registro público), ese es el tipo, sin importar lo que se envíe. */
    @Transactional
    public UsuarioResponse crear(UsuarioRequest r, TipoUsuario forzarTipo) {
        String run = r.run().trim().toUpperCase();
        String correo = r.correo().trim().toLowerCase();
        validarDominio(correo);
        if (r.contrasena() == null || r.contrasena().isBlank()) {
            throw new SolicitudInvalidaException("La contraseña es obligatoria (entre 4 y 10 caracteres)");
        }
        if (repositorio.existsById(run)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese RUN");
        }
        if (repositorio.existsByCorreo(correo)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese correo");
        }
        Usuario u = new Usuario();
        u.setRun(run);
        u.setContrasena(encoder.encode(r.contrasena()));
        u.setTipoUsuario(forzarTipo != null ? forzarTipo
                : (r.tipoUsuario() != null ? r.tipoUsuario() : TipoUsuario.CLIENTE));
        copiarDatos(u, r, correo);
        return aRespuesta(repositorio.save(u));
    }

    /** Edita un usuario. El RUN no cambia; la contraseña solo cambia si se envía una nueva. */
    @Transactional
    public UsuarioResponse actualizar(String run, UsuarioRequest r) {
        Usuario u = buscar(run);
        String correo = r.correo().trim().toLowerCase();
        validarDominio(correo);
        if (repositorio.existsByCorreoAndRunNot(correo, u.getRun())) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese correo");
        }
        if (r.contrasena() != null && !r.contrasena().isBlank()) {
            u.setContrasena(encoder.encode(r.contrasena()));
        }
        if (r.tipoUsuario() != null) {
            u.setTipoUsuario(r.tipoUsuario());
        }
        copiarDatos(u, r, correo);
        return aRespuesta(repositorio.save(u));
    }

    @Transactional
    public void eliminar(String run) {
        repositorio.delete(buscar(run));
    }

    // ---------- privados ----------

    private Usuario buscar(String run) {
        return repositorio.findById(run.trim().toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un usuario con el RUN " + run));
    }

    private void copiarDatos(Usuario u, UsuarioRequest r, String correo) {
        u.setNombre(r.nombre().trim());
        u.setApellidos(r.apellidos().trim());
        u.setCorreo(correo);
        u.setRegion(r.region());
        u.setComuna(r.comuna());
        u.setDireccion(r.direccion());
    }

    private void validarDominio(String correo) {
        String dominio = correo.substring(correo.indexOf('@') + 1);
        if (!DOMINIOS_PERMITIDOS.contains(dominio)) {
            throw new SolicitudInvalidaException("Solo se aceptan correos de: " + String.join(", ", DOMINIOS_PERMITIDOS));
        }
    }

    public static UsuarioResponse aRespuesta(Usuario u) {
        return new UsuarioResponse(u.getRun(), u.getNombre(), u.getApellidos(), u.getCorreo(),
                u.getRegion(), u.getComuna(), u.getDireccion(), u.getTipoUsuario());
    }
}
