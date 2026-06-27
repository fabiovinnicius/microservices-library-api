package autor_ms.demo.controller;

import autor_ms.demo.exception.ErroResposta;
import autor_ms.demo.model.Autor;
import autor_ms.demo.repository.AutorRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/autores")
public class AutorController {

    private final AutorRepository repository;

    public AutorController(AutorRepository repository) {
        this.repository = repository;
    }

    // HU-01 – Listar autores
    @GetMapping
    public List<Autor> listar() {
        return repository.findAll();
    }

    // HU-02 – Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Autor autor = repository.findById(id).orElse(null);
        if (autor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErroResposta.de("Autor não encontrado: " + id));
        }
        return ResponseEntity.ok(autor);
    }

    // HU-03 – Cadastrar
    @PostMapping
    public ResponseEntity<Autor> cadastrar(@Valid @RequestBody Autor autor) {
        Autor salvo = repository.save(autor);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // HU-04 – Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Autor dados) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErroResposta.de("Autor não encontrado: " + id));
        }
        Autor autor = repository.findById(id).get();
        autor.setNome(dados.getNome());
        autor.setNacionalidade(dados.getNacionalidade());
        autor.setAnoNascimento(dados.getAnoNascimento());
        return ResponseEntity.ok(repository.save(autor));
    }

    // HU-05 – Excluir
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErroResposta.de("Autor não encontrado: " + id));
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
