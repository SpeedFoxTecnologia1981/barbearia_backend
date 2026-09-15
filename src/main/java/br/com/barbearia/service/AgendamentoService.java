package br.com.barbearia.service;

import br.com.barbearia.dto.agendamento.*;
import br.com.barbearia.entity.*;
import br.com.barbearia.enums.StatusAgendamento;
import br.com.barbearia.exception.*;
import br.com.barbearia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {
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

        LocalTime inicio = request.hora();
        LocalTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());

        List<Agendamento> existentes = agendamentoRepository
            .findByBarbeiroIdAndDataAgendamento(barbeiro.getId(), request.data());

        boolean conflito = existentes.stream()
            .filter(a -> a.getStatus() != StatusAgendamento.CANCELADO)
            .anyMatch(a -> inicio.isBefore(a.getHoraFim()) && fim.isAfter(a.getHoraInicio()));

        if (conflito)
            throw new BusinessException("Este horário conflita com outro agendamento");

        Agendamento agendamento = Agendamento.builder()
            .usuario(usuario).barbeiro(barbeiro).servico(servico)
            .dataAgendamento(request.data()).horaInicio(inicio).horaFim(fim)
            .status(StatusAgendamento.CONFIRMADO).build();

        agendamento = agendamentoRepository.save(agendamento);
        notificacaoService.enviarConfirmacao(agendamento);
        return toResponse(agendamento);
    }

    private AgendamentoResponseDTO toResponse(Agendamento a) {
        return new AgendamentoResponseDTO(a.getId(), a.getUsuario().getNome(),
            a.getBarbeiro().getNome(), a.getServico().getNome(),
            a.getDataAgendamento(), a.getHoraInicio(), a.getHoraFim(), a.getStatus().name());
    }
}
