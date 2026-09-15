package br.com.barbearia.dto.agendamento;
import jakarta.validation.constraints.*;
import java.time.*;
public record AgendamentoRequestDTO(
    @NotNull Long barbeiroId,
    @NotNull Long servicoId,
    @NotNull @FutureOrPresent LocalDate data,
    @NotNull LocalTime hora
) {}
