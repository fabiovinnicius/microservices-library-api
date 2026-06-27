package livro_ms.demo.repository;

import livro_ms.demo.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByDisponivel(Boolean disponivel);
}
