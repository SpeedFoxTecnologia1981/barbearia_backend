package br.com.barbearia.controller;

import br.com.barbearia.dto.barbeiro.BarbeiroResponseDTO;
import br.com.barbearia.service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/barbeiros")
@RequiredArgsConstructor
public class BarbeiroController {
    private final CatalogoService catalogoService;

    @GetMapping
    public ResponseEntity<List<BarbeiroResponseDTO>> listar() {
        return ResponseEntity.ok(catalogoService.listarBarbeiros());
    }
}
