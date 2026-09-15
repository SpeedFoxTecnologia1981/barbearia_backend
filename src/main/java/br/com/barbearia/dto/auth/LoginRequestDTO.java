package br.com.barbearia.dto.auth;
import jakarta.validation.constraints.NotBlank;
public record LoginRequestDTO(@NotBlank String nome, @NotBlank String telefone) {}
