package front_ms.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${autor.ms.url}")
    private String autorUrl;

    @Value("${livro.ms.url}")
    private String livroUrl;

    @Bean
    public RestClient autorRestClient() {
        return RestClient.builder()
                .baseUrl(autorUrl)
                .build();
    }

    @Bean
    public RestClient livroRestClient() {
        return RestClient.builder()
                .baseUrl(livroUrl)
                .build();
    }
}