package br.com.barbearia.dto.auth;
public record LoginResponseDTO(String token, Long usuarioId, String nome, String perfil) {}
