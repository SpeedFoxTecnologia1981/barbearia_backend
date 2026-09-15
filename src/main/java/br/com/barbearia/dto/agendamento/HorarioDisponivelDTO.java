package br.com.barbearia.dto.agendamento;
import java.time.LocalTime;
public record HorarioDisponivelDTO(LocalTime hora, Boolean disponivel) {}
