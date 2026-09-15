package br.com.barbearia.dto.barbeiro;
import jakarta.validation.constraints.NotBlank;
public record BarbeiroRequestDTO(@NotBlank String nome, String telefone) {}
