package front_ms.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import front_ms.demo.client.AutorClient;
import front_ms.demo.client.LivroClient;
import front_ms.demo.dto.AutorDTO;
import front_ms.demo.dto.LivroDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/livros")
public class LivroWebController {

    private final LivroClient livroClient;
    private final AutorClient autorClient;

    public LivroWebController(LivroClient livroClient, AutorClient autorClient) {
        this.livroClient = livroClient;
        this.autorClient = autorClient;
    }

    // Lista todos os livros (com filtro opcional por disponibilidade)
    @GetMapping
    public String listar(@RequestParam(required = false) Boolean disponivel, Model model) {
        List<LivroDTO> livros = livroClient.listar(disponivel);
        preencherNomesAutores(livros);
        model.addAttribute("livros", livros);
        model.addAttribute("disponivelSelecionado", disponivel);
        return "livros/lista";
    }

    // Exibe o detalhe de um livro
    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        LivroDTO livro = livroClient.buscarPorId(id);
        if (livro == null) {
            model.addAttribute("erro", "Livro não encontrado.");
            return "livros/detalhe";
        }
        preencherNomesAutores(List.of(livro));
        model.addAttribute("livro", livro);
        return "livros/detalhe";
    }

    // Abre formulário de cadastro
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("livro", new LivroDTO());
        model.addAttribute("autores", autorClient.listarTodos());

        return "livros/formulario";
    }

    // Salva livro
    @PostMapping
    public String salvar(@ModelAttribute LivroDTO livro,
                         RedirectAttributes redirectAttributes) {

        livroClient.cadastrar(livro);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Livro cadastrado com sucesso!"
        );

        return "redirect:/livros";
    }

    // Abre edição
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id,
                         Model model) {

        model.addAttribute("livro", livroClient.buscarPorId(id));
        model.addAttribute("autores", autorClient.listarTodos());

        return "livros/formulario";
    }

    // Atualiza
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute LivroDTO livro,
                            RedirectAttributes redirectAttributes) {

        livroClient.atualizar(id, livro);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Livro atualizado com sucesso!"
        );

        return "redirect:/livros";
    }

    // Exclui
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {

        livroClient.excluir(id);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Livro excluído com sucesso!"
        );

        return "redirect:/livros";
    }

    // Busca os autores e preenche o nome de cada um nos livros, pra exibição nas telas
    private void preencherNomesAutores(List<LivroDTO> livros) {
        List<AutorDTO> autores = autorClient.listarTodos();
        Map<Long, String> nomesPorId = autores.stream()
                .collect(Collectors.toMap(AutorDTO::getId, AutorDTO::getNome));

        for (LivroDTO livro : livros) {
            livro.setNomeAutor(nomesPorId.getOrDefault(livro.getAutorId(), "Desconhecido"));
        }
    }

}