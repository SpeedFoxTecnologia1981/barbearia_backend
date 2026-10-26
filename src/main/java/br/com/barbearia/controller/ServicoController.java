package br.com.barbearia.controller;

import br.com.barbearia.dto.servico.ServicoResponseDTO;
import br.com.barbearia.service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
@RequiredArgsConstructor
public class ServicoController {
    private final CatalogoService catalogoService;

    @GetMapping
    public ResponseEntity<List<ServicoResponseDTO>> listar() {
        return ResponseEntity.ok(catalogoService.listarServicos());
    }
}
