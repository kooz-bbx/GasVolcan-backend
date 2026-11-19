package cl.gasvolcan.api.security;

import cl.gasvolcan.api.exception.CredencialesInvalidasException;
import cl.gasvolcan.api.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

/**
 * Genera y verifica los tokens JWT, firmados con HMAC-SHA256.
 *  - access token  (corto, 30 min): se envía en cada petición en el header Authorization: Bearer ...
 *  - refresh token (largo, 7 días): solo sirve para pedir un par de tokens nuevo en /auth/refresh
 */
@Service
public class JwtService {

    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";

    private final SecretKey clave;
    private final Duration duracionAccess;
    private final Duration duracionRefresh;

    public JwtService(@Value("${jwt.secret}") String secreto,
                      @Value("${jwt.access-minutos}") long accessMinutos,
                      @Value("${jwt.refresh-dias}") long refreshDias) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.duracionAccess = Duration.ofMinutes(accessMinutos);
        this.duracionRefresh = Duration.ofDays(refreshDias);
    }

    public String generarAccessToken(Usuario u) {
        return construir(u, ACCESS, duracionAccess);
    }

    public String generarRefreshToken(Usuario u) {
        return construir(u, REFRESH, duracionRefresh);
    }

    /** Devuelve el correo (subject) si es un access token válido; si no, lanza 401. */
    public String correoDeAccessToken(String token) {
        return leer(token, ACCESS).getSubject();
    }

    /** Devuelve el correo (subject) si es un refresh token válido; si no, lanza 401. */
    public String correoDeRefreshToken(String token) {
        return leer(token, REFRESH).getSubject();
    }

    private String construir(Usuario u, String tipo, Duration duracion) {
        long ahora = System.currentTimeMillis();
        return Jwts.builder()
                .subject(u.getCorreo())
                .claim("rol", u.getTipoUsuario().name())
                .claim("tipo", tipo)
                .issuedAt(new Date(ahora))
                .expiration(new Date(ahora + duracion.toMillis()))
                .signWith(clave)
                .compact();
    }

    /** Verifica la firma y la fecha de expiración, y que sea del tipo esperado. */
    private Claims leer(String token, String tipoEsperado) {
        try {
            Claims claims = Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
            if (!tipoEsperado.equals(claims.get("tipo", String.class))) {
                throw new CredencialesInvalidasException("Token de tipo incorrecto");
            }
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new CredencialesInvalidasException("Token inválido o expirado");
        }
    }
}
