package br.com.barbearia.dto.servico;
import java.math.BigDecimal;
public record ServicoResponseDTO(Long id, String nome, String descricao, Integer duracaoMinutos, BigDecimal valor, Boolean ativo) {}
