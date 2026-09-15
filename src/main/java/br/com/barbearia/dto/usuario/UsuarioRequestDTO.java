package br.com.barbearia.dto.usuario;

import jakarta.validation.constraints.*;

public record UsuarioRequestDTO(
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve possuir entre 3 e 150 caracteres")
    String nome,

    @Size(max = 100)
    String apelido,

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = ".*\\d.*", message = "Telefone inválido")
    String telefone
) {}
