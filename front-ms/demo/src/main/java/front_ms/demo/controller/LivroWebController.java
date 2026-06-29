package front_ms.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import front_ms.demo.client.AutorClient;
import front_ms.demo.client.LivroClient;
import front_ms.demo.dto.LivroDTO;

@Controller
@RequestMapping("/livros")
public class LivroWebController {

    private final LivroClient livroClient;
    private final AutorClient autorClient;

    public LivroWebController(LivroClient livroClient, AutorClient autorClient) {
        this.livroClient = livroClient;
        this.autorClient = autorClient;
    }

    // Lista todos os livros
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("livros", livroClient.listar(null));
        return "livros/lista";
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

}