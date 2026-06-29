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
import front_ms.demo.dto.AutorDTO;

@Controller
@RequestMapping("/autores")
public class AutorWebController {

    private final AutorClient autorClient;

    public AutorWebController(AutorClient autorClient) {
        this.autorClient = autorClient;
    }

    // Listar autores
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("autores", autorClient.listarTodos());
        return "autores/lista";
    }

    // Abrir formulário de cadastro
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("autor", new AutorDTO());
        return "autores/formulario";
    }

    // Salvar autor
    @PostMapping
    public String salvar(@ModelAttribute AutorDTO autor,
                         RedirectAttributes redirectAttributes) {

        autorClient.cadastrar(autor);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Autor cadastrado com sucesso!"
        );

        return "redirect:/autores";
    }

    // Abrir formulário de edição
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id,
                         Model model) {

        model.addAttribute(
                "autor",
                autorClient.buscarPorId(id)
        );

        return "autores/formulario";
    }

    // Atualizar autor
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute AutorDTO autor,
                            RedirectAttributes redirectAttributes) {

        autorClient.atualizar(id, autor);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Autor atualizado com sucesso!"
        );

        return "redirect:/autores";
    }

    // Excluir autor
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {

        autorClient.excluir(id);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Autor excluído com sucesso!"
        );

        return "redirect:/autores";
    }

}