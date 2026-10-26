package br.com.barbearia.service;

import br.com.barbearia.dto.agendamento.*;
import br.com.barbearia.entity.*;
import br.com.barbearia.enums.StatusAgendamento;
import br.com.barbearia.exception.*;
import br.com.barbearia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {
    // Funcionamento: segunda a sábado, 07:30 às 20:30 (domingo fechado).
    private static final LocalTime ABERTURA = LocalTime.of(7, 30);
    private static final LocalTime FECHAMENTO = LocalTime.of(20, 30);
    private static final int INTERVALO_MINUTOS = 30;

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioService usuarioService;
    private final BarbeiroRepository barbeiroRepository;
    private final ServicoRepository servicoRepository;
    private final NotificacaoService notificacaoService;

    @Transactional
    public AgendamentoResponseDTO criar(Long usuarioId, AgendamentoRequestDTO request) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        Barbeiro barbeiro = barbeiroRepository.findById(request.barbeiroId())
            .orElseThrow(() -> new ResourceNotFoundException("Barbeiro não encontrado"));
        Servico servico = servicoRepository.findById(request.servicoId())
            .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        if (!Boolean.TRUE.equals(barbeiro.getAtivo()))
            throw new BusinessException("Barbeiro indisponível");
        if (!Boolean.TRUE.equals(servico.getAtivo()))
            throw new BusinessException("Serviço indisponível");

        if (request.data().getDayOfWeek() == DayOfWeek.SUNDAY)
            throw new BusinessException("Aos domingos estamos fechados");

        LocalTime inicio = request.hora();
        LocalTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());

        if (inicio.isBefore(ABERTURA) || fim.isBefore(inicio) || fim.isAfter(FECHAMENTO))
            throw new BusinessException("Horário fora do funcionamento (07:30 às 20:30)");

        if (request.data().equals(LocalDate.now()) && !inicio.isAfter(LocalTime.now()))
            throw new BusinessException("Este horário já passou");

        if (existeConflito(barbeiro.getId(), request.data(), inicio, fim))
            throw new BusinessException("Este horário conflita com outro agendamento");

        Agendamento agendamento = Agendamento.builder()
            .usuario(usuario).barbeiro(barbeiro).servico(servico)
            .dataAgendamento(request.data()).horaInicio(inicio).horaFim(fim)
            .status(StatusAgendamento.CONFIRMADO).build();

        agendamento = agendamentoRepository.save(agendamento);
        notificacaoService.enviarConfirmacao(agendamento);
        return toResponse(agendamento);
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponseDTO> listarDoUsuario(Long usuarioId) {
        return agendamentoRepository
            .findByUsuarioIdOrderByDataAgendamentoDescHoraInicioDesc(usuarioId)
            .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<HorarioDisponivelDTO> listarHorarios(Long barbeiroId, Long servicoId, LocalDate data) {
        Barbeiro barbeiro = barbeiroRepository.findById(barbeiroId)
            .orElseThrow(() -> new ResourceNotFoundException("Barbeiro não encontrado"));
        Servico servico = servicoRepository.findById(servicoId)
            .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));

        if (data.getDayOfWeek() == DayOfWeek.SUNDAY) return List.of();

        List<Agendamento> ativos = agendamentoRepository
            .findByBarbeiroIdAndDataAgendamento(barbeiro.getId(), data).stream()
            .filter(a -> a.getStatus() != StatusAgendamento.CANCELADO)
            .toList();

        boolean hoje = data.equals(LocalDate.now());
        LocalTime agora = LocalTime.now();
        int duracao = servico.getDuracaoMinutos();

        List<HorarioDisponivelDTO> horarios = new ArrayList<>();
        for (LocalTime h = ABERTURA; !h.plusMinutes(duracao).isAfter(FECHAMENTO); h = h.plusMinutes(INTERVALO_MINUTOS)) {
            LocalTime inicio = h;
            LocalTime fim = h.plusMinutes(duracao);
            boolean livre = ativos.stream()
                .noneMatch(a -> inicio.isBefore(a.getHoraFim()) && fim.isAfter(a.getHoraInicio()));
            if (hoje && !inicio.isAfter(agora)) livre = false;
            horarios.add(new HorarioDisponivelDTO(inicio, livre));
        }
        return horarios;
    }

    @Transactional
    public AgendamentoResponseDTO cancelar(Long usuarioId, Long agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
            .filter(a -> a.getUsuario().getId().equals(usuarioId))
            .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        if (agendamento.getStatus() == StatusAgendamento.CANCELADO)
            throw new BusinessException("Este agendamento já foi cancelado");
        if (agendamento.getStatus() == StatusAgendamento.FINALIZADO
                || agendamento.getStatus() == StatusAgendamento.NAO_COMPARECEU)
            throw new BusinessException("Este agendamento não pode mais ser cancelado");

        LocalDateTime inicio = LocalDateTime.of(agendamento.getDataAgendamento(), agendamento.getHoraInicio());
        if (LocalDateTime.now().isAfter(inicio.minusHours(1)))
            throw new BusinessException("O cancelamento é permitido até 1 hora antes do horário");

        agendamento.setStatus(StatusAgendamento.CANCELADO);
        return toResponse(agendamentoRepository.save(agendamento));
    }

    private boolean existeConflito(Long barbeiroId, LocalDate data, LocalTime inicio, LocalTime fim) {
        return agendamentoRepository.findByBarbeiroIdAndDataAgendamento(barbeiroId, data).stream()
            .filter(a -> a.getStatus() != StatusAgendamento.CANCELADO)
            .anyMatch(a -> inicio.isBefore(a.getHoraFim()) && fim.isAfter(a.getHoraInicio()));
    }

    private AgendamentoResponseDTO toResponse(Agendamento a) {
        return new AgendamentoResponseDTO(a.getId(), a.getUsuario().getNome(),
            a.getBarbeiro().getNome(), a.getServico().getNome(),
            a.getDataAgendamento(), a.getHoraInicio(), a.getHoraFim(), a.getStatus().name(),
            a.getServico().getValor());
    }
}
