package cl.gasvolcan.api.repository;

import cl.gasvolcan.api.model.TipoUsuario;
import cl.gasvolcan.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByCorreoAndRunNot(String correo, String run);

    List<Usuario> findByTipoUsuario(TipoUsuario tipoUsuario);
}
