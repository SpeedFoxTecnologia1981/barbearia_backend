package br.com.barbearia.controller;

import br.com.barbearia.dto.agendamento.*;
import br.com.barbearia.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {
    private final AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> criar(
            Authentication authentication,
            @Valid @RequestBody AgendamentoRequestDTO request) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(agendamentoService.criar(usuarioId, request));
    }

    /** Agendamentos do usuário logado (mais recentes primeiro). */
    @GetMapping("/meus")
    public ResponseEntity<List<AgendamentoResponseDTO>> meus(Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(agendamentoService.listarDoUsuario(usuarioId));
    }

    /** Horários do dia para um barbeiro e serviço (público). */
    @GetMapping("/horarios")
    public ResponseEntity<List<HorarioDisponivelDTO>> horarios(
            @RequestParam Long barbeiroId,
            @RequestParam Long servicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(agendamentoService.listarHorarios(barbeiroId, servicoId, data));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AgendamentoResponseDTO> cancelar(
            Authentication authentication, @PathVariable Long id) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(agendamentoService.cancelar(usuarioId, id));
    }
}
