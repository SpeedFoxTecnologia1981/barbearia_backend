package br.com.barbearia.controller;

import br.com.barbearia.dto.agendamento.*;
import br.com.barbearia.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
}
