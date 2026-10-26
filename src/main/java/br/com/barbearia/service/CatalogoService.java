package br.com.barbearia.service;

import br.com.barbearia.dto.barbeiro.BarbeiroResponseDTO;
import br.com.barbearia.dto.servico.ServicoResponseDTO;
import br.com.barbearia.repository.BarbeiroRepository;
import br.com.barbearia.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogoService {
    private final BarbeiroRepository barbeiroRepository;
    private final ServicoRepository servicoRepository;

    @Transactional(readOnly = true)
    public List<BarbeiroResponseDTO> listarBarbeiros() {
        return barbeiroRepository.findByAtivoTrue().stream()
            .map(b -> new BarbeiroResponseDTO(b.getId(), b.getNome(), b.getTelefone(), b.getAtivo()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ServicoResponseDTO> listarServicos() {
        return servicoRepository.findByAtivoTrue().stream()
            .map(s -> new ServicoResponseDTO(s.getId(), s.getNome(), s.getDescricao(),
                s.getDuracaoMinutos(), s.getValor(), s.getAtivo()))
            .toList();
    }
}
