package br.com.barbearia.service;

import br.com.barbearia.dto.usuario.*;
import br.com.barbearia.entity.Usuario;
import br.com.barbearia.exception.*;
import br.com.barbearia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO request) {
        String telefone = normalizarTelefone(request.telefone());
        if (usuarioRepository.existsByTelefone(telefone))
            throw new BusinessException("Já existe um usuário cadastrado com este telefone");

        Usuario usuario = Usuario.builder()
            .nome(request.nome()).apelido(request.apelido()).telefone(telefone).build();
        usuario = usuarioRepository.save(usuario);
        return toResponse(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private String normalizarTelefone(String telefone) { return telefone.replaceAll("\\D", ""); }

    private UsuarioResponseDTO toResponse(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getApelido(),
            usuario.getTelefone(), usuario.getPerfil().name());
    }
}
