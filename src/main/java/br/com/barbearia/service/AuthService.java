package br.com.barbearia.service;

import br.com.barbearia.dto.auth.*;
import br.com.barbearia.entity.Usuario;
import br.com.barbearia.exception.BusinessException;
import br.com.barbearia.repository.UsuarioRepository;
import br.com.barbearia.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO request) {
        String telefone = request.telefone().replaceAll("\\D", "");
        Usuario usuario = usuarioRepository.findByNomeAndTelefone(request.nome(), telefone)
            .orElseThrow(() -> new BusinessException("Nome ou telefone inválido"));

        if (!Boolean.TRUE.equals(usuario.getAtivo()))
            throw new BusinessException("Usuário inativo");

        String token = jwtService.gerarToken(usuario.getId(), usuario.getNome(), usuario.getPerfil().name());
        return new LoginResponseDTO(token, usuario.getId(), usuario.getNome(), usuario.getPerfil().name());
    }
}
