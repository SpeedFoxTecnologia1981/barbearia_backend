package br.com.barbearia.repository;

import br.com.barbearia.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByUsuarioIdOrderByDataAgendamentoDescHoraInicioDesc(Long usuarioId);
    List<Agendamento> findByBarbeiroIdAndDataAgendamento(Long barbeiroId, LocalDate data);
}
