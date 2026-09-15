package br.com.barbearia.dto.agendamento;
import java.time.*;
public record AgendamentoResponseDTO(
    Long id, String usuario, String barbeiro, String servico,
    LocalDate data, LocalTime horaInicio, LocalTime horaFim, String status
) {}
