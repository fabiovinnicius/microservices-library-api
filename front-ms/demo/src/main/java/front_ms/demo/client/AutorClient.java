package front_ms.demo.client;

import front_ms.demo.dto.AutorDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;

@Component
public class AutorClient {

    private final RestClient restClient;

    public AutorClient(@Qualifier("autorRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<AutorDTO> listarTodos() {
        try {
            return restClient.get()
                    .uri("/api/autores")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AutorDTO>>() {});
        } catch (RestClientException e) {
            return Collections.emptyList();
        }
    }

    public AutorDTO buscarPorId(Long id) {
        try {
            return restClient.get()
                    .uri("/api/autores/{id}", id)
                    .retrieve()
                    .body(AutorDTO.class);
        } catch (RestClientException e) {
            return null;
        }
    }

    public AutorDTO cadastrar(AutorDTO autor) {
        try {
            return restClient.post()
                    .uri("/api/autores")
                    .body(autor)
                    .retrieve()
                    .body(AutorDTO.class);
        } catch (RestClientException e) {
            return null;
        }
    }

    public AutorDTO atualizar(Long id, AutorDTO autor) {
        try {
            return restClient.put()
                    .uri("/api/autores/{id}", id)
                    .body(autor)
                    .retrieve()
                    .body(AutorDTO.class);
        } catch (RestClientException e) {
            return null;
        }
    }

    public void excluir(Long id) {
        try {
            restClient.delete()
                    .uri("/api/autores/{id}", id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            System.out.println("Erro ao excluir autor: " + e.getMessage());
        }
    }
}