package com.pokedex.apipokedex.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.token.expiration:86400000}")
    private long jwtExpiration;

    // ==========================================
    // 1. MÉTODOS PARA CREAR EL TOKEN (Sintaxis v0.12+)
    // ==========================================
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims) // <-- Nueva sintaxis
                .subject(userDetails.getUsername()) // <-- Nueva sintaxis
                .issuedAt(new Date(System.currentTimeMillis())) // <-- Nueva sintaxis
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // <-- Nueva sintaxis
                .signWith(getSignInKey()) // <-- Nueva sintaxis
                .compact();
    }

    // ==========================================
    // 2. MÉTODOS PARA LEER EL TOKEN (Sintaxis v0.12+)
    // ==========================================
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser() // <-- Aquí está tu arreglo
                .verifyWith(getSignInKey()) // <-- Nueva forma de validar
                .build()
                .parseSignedClaims(token) // <-- Cambió de parseClaimsJws
                .getPayload(); // <-- Cambió de getBody
    }

    // ==========================================
    // 3. MÉTODO DE SEGURIDAD INTERNA
    // ==========================================
    private SecretKey getSignInKey() { // <-- Ahora devuelve SecretKey obligatoriamente
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
