package front_ms.demo.client;

import front_ms.demo.dto.LivroDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;

@Component
public class LivroClient {

    private final RestClient restClient;

    public LivroClient(@Qualifier("livroRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<LivroDTO> listar(Boolean disponivel) {
        try {
            String uri = "/api/livros";

            if (disponivel != null) {
                uri += "?disponivel=" + disponivel;
            }

            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<LivroDTO>>() {});
        } catch (RestClientException e) {
            return Collections.emptyList();
        }
    }

    public LivroDTO buscarPorId(Long id) {
        try {
            return restClient.get()
                    .uri("/api/livros/{id}", id)
                    .retrieve()
                    .body(LivroDTO.class);
        } catch (RestClientException e) {
            return null;
        }
    }

    public LivroDTO cadastrar(LivroDTO livro) {
        try {
            return restClient.post()
                    .uri("/api/livros")
                    .body(livro)
                    .retrieve()
                    .body(LivroDTO.class);
        } catch (RestClientException e) {
            return null;
        }
    }

    public LivroDTO atualizar(Long id, LivroDTO livro) {
        try {
            return restClient.put()
                    .uri("/api/livros/{id}", id)
                    .body(livro)
                    .retrieve()
                    .body(LivroDTO.class);
        } catch (RestClientException e) {
            return null;
        }
    }

    public void excluir(Long id) {
        try {
            restClient.delete()
                    .uri("/api/livros/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            System.out.println("Erro ao excluir livro: " + e.getMessage());
        }
    }
}