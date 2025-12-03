package com.riwi.H4.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para la generación y validación de JWT.
 *
 * Gestiona la creación, el parseo y la validación de tokens JWT,
 * utilizando una clave secreta y un tiempo de expiración configurables.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    // --- Generación de Token ---

    /**
     * Genera un JWT para un UserDetails dado.
     * @param userDetails Los detalles del usuario para incluir en el token.
     * @return El string del JWT generado.
     */
    public String generateToken(UserDetails userDetails) {
        // Incluye el rol del usuario como un claim
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().stream().findFirst().orElse(null));

        return buildToken(claims, userDetails, jwtExpiration);
    }

    /**
     * Construye el JWT con claims, sujeto, emisor y expiración.
     * @param extraClaims Claims adicionales para el token.
     * @param userDetails Los detalles del usuario (sujeto).
     * @param expiration El tiempo de expiración en milisegundos.
     * @return El string del JWT.
     */
    public String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuer(jwtIssuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    // --- Validación y Extracción de Token ---

    /**
     * Valida un JWT.
     * @param token El string del JWT.
     * @param userDetails Los detalles del usuario para validar.
     * @return True si el token es válido, false en caso contrario.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Extrae el nombre de usuario (subject) de un JWT.
     * @param token El string del JWT.
     * @return El nombre de usuario.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae un claim específico de un JWT.
     * @param token El string del JWT.
     * @param claimsResolver Función para resolver el claim deseado.
     * @param <T> Tipo del claim.
     * @return El claim extraído.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims de un JWT.
     * @param token El string del JWT.
     * @return Todos los claims.
     */
    public Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Verifica si un JWT ha expirado.
     * @param token El string del JWT.
     * @return True si el token ha expirado, false en caso contrario.
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración de un JWT.
     * @param token El string del JWT.
     * @return La fecha de expiración.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Recupera la clave de firma desde el secreto codificado en base64.
     * @return La SecretKey.
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public long getJwtExpiration() {
        return jwtExpiration;
    }
}
