package livro_ms.demo.controller;

import livro_ms.demo.exception.ErroResposta;
import livro_ms.demo.model.Livro;
import livro_ms.demo.repository.LivroRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/livros")
public class LivroController {

    private final LivroRepository repository;

    public LivroController(LivroRepository repository) {
        this.repository = repository;
    }

    // HU-06 – Listar livros (com filtro opcional por disponibilidade)
    @GetMapping
    public List<Livro> listar(@RequestParam Optional<Boolean> disponivel) {
        if (disponivel.isPresent()) {
            return repository.findByDisponivel(disponivel.get());
        }
        return repository.findAll();
    }

    // HU-07 – Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Livro livro = repository.findById(id).orElse(null);
        if (livro == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErroResposta.de("Livro não encontrado: " + id));
        }
        return ResponseEntity.ok(livro);
    }

    // HU-08 – Cadastrar
    @PostMapping
    public ResponseEntity<Livro> cadastrar(@Valid @RequestBody Livro livro) {
        if (livro.getDisponivel() == null) {
            livro.setDisponivel(true);
        }
        Livro salvo = repository.save(livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // HU-09 – Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Livro dados) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErroResposta.de("Livro não encontrado: " + id));
        }
        Livro livro = repository.findById(id).get();
        livro.setTitulo(dados.getTitulo());
        livro.setGenero(dados.getGenero());
        livro.setAnoPublicacao(dados.getAnoPublicacao());
        livro.setDisponivel(dados.getDisponivel());
        livro.setAutorId(dados.getAutorId());
        return ResponseEntity.ok(repository.save(livro));
    }

    // HU-10 – Excluir
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErroResposta.de("Livro não encontrado: " + id));
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
