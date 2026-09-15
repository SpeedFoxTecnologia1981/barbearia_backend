package br.com.barbearia.dto.servico;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record ServicoRequestDTO(
    @NotBlank String nome,
    String descricao,
    @NotNull @Min(10) Integer duracaoMinutos,
    @NotNull @DecimalMin("0.00") BigDecimal valor
) {}
