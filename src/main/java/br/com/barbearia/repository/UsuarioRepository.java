package br.com.barbearia.repository;

import br.com.barbearia.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByTelefone(String telefone);
    Optional<Usuario> findByNomeAndTelefone(String nome, String telefone);
    boolean existsByTelefone(String telefone);
}
