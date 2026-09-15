package br.com.barbearia.repository;
import br.com.barbearia.entity.Barbeiro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BarbeiroRepository extends JpaRepository<Barbeiro, Long> {
    List<Barbeiro> findByAtivoTrue();
}
