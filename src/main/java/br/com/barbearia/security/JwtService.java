package br.com.barbearia.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration}") private Long expiration;

    public String gerarToken(Long usuarioId, String nome, String perfil) {
        Date agora = new Date();
        Date validade = new Date(agora.getTime() + expiration);
        return Jwts.builder()
            .subject(usuarioId.toString())
            .claim("nome", nome)
            .claim("perfil", perfil)
            .issuedAt(agora)
            .expiration(validade)
            .signWith(getKey())
            .compact();
    }

    public Long extrairUsuarioId(String token) {
        return Long.valueOf(extrairClaims(token).getSubject());
    }

    public boolean tokenValido(String token) {
        try { extrairClaims(token); return true; }
        catch (JwtException | IllegalArgumentException ex) { return false; }
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser().verifyWith(getKey()).build()
            .parseSignedClaims(token).getPayload();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
