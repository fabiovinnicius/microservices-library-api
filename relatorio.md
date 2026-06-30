# Relatório Técnico — Sistema de Biblioteca
## Arquitetura de Microsserviços com Spring Boot, Thymeleaf, PostgreSQL, Docker e Nginx

**Disciplina:** Desenvolvimento Web com Java  
**Instituição:** Instituto Federal de Educação, Ciência e Tecnologia do Ceará (IFCE)  
**Curso:** Ciência da Computação  
**Integrantes:** Fábio e Kelton  

---

## 1. Introdução

Este relatório descreve o desenvolvimento do Sistema de Biblioteca, trabalho final da disciplina de Desenvolvimento Web com Java. O objetivo foi refatorar um sistema monolítico de cadastro de livros para uma arquitetura de microsserviços, aplicando conceitos modernos de desenvolvimento de software distribuído.

O sistema foi construído com quatro serviços independentes orquestrados via Docker Compose, com comunicação via HTTP REST e interface web renderizada com Thymeleaf. O resultado é uma aplicação que pode ser iniciada integralmente com um único comando, seguindo as práticas de infraestrutura como código.

---

## 2. Visão Geral da Arquitetura

A arquitetura adotada segue o padrão de microsserviços, onde cada componente possui responsabilidade única, banco de dados próprio e pode ser desenvolvido, implantado e escalado de forma independente.

```
[Browser]
    |
[nginx :80] — API Gateway
    |— /              → front-ms  :8080
    |— /api/livros    → livro-ms  :8081
    |— /api/autores   → autor-ms  :8082
```

O Nginx atua como API Gateway, sendo o único ponto de entrada da aplicação. Ele roteia as requisições para os microsserviços corretos com base no caminho da URL, centralizando o controle de acesso e eliminando a necessidade de o cliente conhecer os endereços internos de cada serviço.

### 2.1 Serviços

O sistema é composto por seis containers Docker:

**autor-ms** é responsável pelo CRUD completo de autores. Expõe uma API REST na porta 8082 e persiste os dados em um banco PostgreSQL exclusivo (`db_autores`). Não possui dependência de nenhum outro microsserviço.

**livro-ms** é responsável pelo CRUD completo de livros, incluindo filtro por disponibilidade. Expõe uma API REST na porta 8081 e persiste os dados em um banco PostgreSQL exclusivo (`db_livros`). Armazena o `autorId` como referência lógica, sem chave estrangeira no banco.

**front-ms** é o único serviço com interface visual. Utiliza Spring Boot com Thymeleaf para renderizar as páginas HTML e consome os outros dois microsserviços via `RestClient`. Não possui banco de dados próprio.

**nginx** atua como API Gateway e proxy reverso, roteando as requisições do browser para os microsserviços corretos com base na URL.

**postgres-livros** e **postgres-autores** são instâncias independentes do PostgreSQL 16, cada uma dedicada ao seu respectivo microsserviço.

---

## 3. Modelos de Dados

### 3.1 Entidade Autor (autor-ms)

| Campo | Tipo | Restrições |
|---|---|---|
| id | BIGSERIAL | Chave primária, gerado automaticamente |
| nome | VARCHAR(100) | NOT NULL |
| nacionalidade | VARCHAR(80) | NOT NULL |
| anoNascimento | INTEGER | NOT NULL, positivo |

### 3.2 Entidade Livro (livro-ms)

| Campo | Tipo | Restrições |
|---|---|---|
| id | BIGSERIAL | Chave primária, gerado automaticamente |
| titulo | VARCHAR(150) | NOT NULL |
| genero | VARCHAR(60) | NOT NULL |
| anoPublicacao | INTEGER | NOT NULL, positivo |
| disponivel | BOOLEAN | NOT NULL, padrão true |
| autorId | BIGINT | NOT NULL (referência lógica) |

O campo `autorId` é uma referência lógica, sem constraint de chave estrangeira no banco de dados. Em arquiteturas de microsserviços, a integridade referencial entre serviços é responsabilidade da camada de aplicação, não do banco de dados. A validação é feita pelo `front-ms`, que consulta o `autor-ms` antes de enviar a requisição de cadastro.

---

## 4. Decisões Técnicas

### 4.1 Comunicação entre serviços

A comunicação entre o `front-ms` e os microsserviços de domínio foi implementada com o `RestClient`, disponível a partir do Spring Boot 3.2. Essa API fluente e síncrona foi escolhida por ser a recomendação atual do Spring para chamadas HTTP cliente-servidor, substituindo o `RestTemplate`.

Cada microsserviço possui um bean `RestClient` configurado com sua URL base via variável de ambiente, permitindo que a mesma imagem Docker funcione tanto localmente quanto em produção sem alteração de código.

### 4.2 Resolução do nome do autor

Seguindo a regra de ouro dos microsserviços, o `livro-ms` não conhece o `autor-ms`. O `front-ms` é responsável por resolver o nome do autor: ao listar os livros, ele busca cada `autorId` via `GET /api/autores/{id}` e injeta o nome no DTO antes de renderizar o template. Caso o autor não seja encontrado (por ter sido excluído, por exemplo), o sistema exibe "Autor removido" sem quebrar a página.

### 4.3 Padrão PRG (Post/Redirect/Get)

Todos os formulários da interface web seguem o padrão PRG: após um POST bem-sucedido, o controller redireciona para a página de listagem com uma mensagem flash de sucesso. Isso evita que o usuário reenvie o formulário ao pressionar F5, prevenindo duplicação de dados.

### 4.4 Persistência com volumes Docker

Os dados dos dois bancos PostgreSQL são persistidos em volumes Docker nomeados (`dados-livros` e `dados-autores`). Isso garante que os dados sobrevivam a reinicializações de containers com `docker compose restart` ou `docker compose down`, sendo removidos apenas com `docker compose down -v`.

### 4.5 Healthcheck nos bancos de dados

O `docker-compose.yml` configura healthchecks nos containers PostgreSQL usando `pg_isready`. Os microsserviços Spring Boot só são iniciados após o banco correspondente estar saudável (`condition: service_healthy`), evitando erros de conexão durante a inicialização.

---

## 5. Histórias de Usuário Implementadas

Todas as 21 histórias de usuário definidas no enunciado foram implementadas:

| ID | Descrição | Serviço |
|---|---|---|
| HU-01 | Listar autores | autor-ms |
| HU-02 | Buscar autor por ID | autor-ms |
| HU-03 | Cadastrar autor | autor-ms |
| HU-04 | Atualizar autor | autor-ms |
| HU-05 | Excluir autor | autor-ms |
| HU-06 | Listar livros | livro-ms |
| HU-07 | Buscar livro por ID | livro-ms |
| HU-08 | Cadastrar livro | livro-ms |
| HU-09 | Atualizar livro | livro-ms |
| HU-10 | Excluir livro | livro-ms |
| HU-11 | Filtrar livros por disponibilidade | livro-ms |
| HU-12 | Tela de listagem de autores | front-ms |
| HU-13 | Formulário de cadastro e edição de autor | front-ms |
| HU-14 | Excluir autor pela interface web | front-ms |
| HU-15 | Tela de listagem de livros com nome do autor | front-ms |
| HU-16 | Formulário de cadastro e edição de livro | front-ms |
| HU-17 | Página de detalhe do livro | front-ms |
| HU-18 | Excluir livro pela interface web | front-ms |
| HU-19 | Subir sistema com um único comando | infraestrutura |
| HU-20 | Persistência de dados entre reinicializações | infraestrutura |
| HU-21 | Roteamento pelo Nginx | infraestrutura |

---

## 6. Dificuldades Encontradas

### 6.1 Configuração e uso do Docker

A principal dificuldade da dupla foi a configuração do ambiente Docker, especialmente por ser o primeiro contato com a ferramenta. Os principais obstáculos enfrentados foram:

**Inicialização do Docker Desktop:** Inicialmente, os comandos `docker compose` falhavam com erro de conexão ao daemon. A causa era que o Docker Desktop precisava estar aberto e totalmente iniciado antes de qualquer comando ser executado.

**Estrutura de pacotes Java:** O Spring Boot só escaneia componentes que estão no mesmo pacote ou subpacotes da classe principal (`DemoApplication`). Como os arquivos foram criados em pacotes com nomenclatura diferente (`autor.ms.demo` vs `autor_ms.demo`), o Spring não encontrava os beans, resultando em `Found 0 JPA repository interfaces`. A solução foi alinhar os pacotes com o nome exato gerado pelo Spring Initializr.

**Build do Docker sem cache:** Durante o desenvolvimento iterativo, o cache do Docker às vezes impedia que alterações no código fossem refletidas na imagem construída. A solução foi sempre utilizar a flag `--no-cache` durante a depuração: `docker compose build --no-cache`.

**Portas e redes internas:** A comunicação entre containers não usa `localhost` — cada serviço é acessado pelo seu nome no `docker-compose.yml` (ex: `http://livro-ms:8081`). Essa diferença causou confusão inicial ao tentar testar os serviços individualmente de fora do Docker.

**Gestão de branches com Git:** Durante o desenvolvimento colaborativo, houve um conflito de branches que resultou em perda temporária de progresso. O trabalho foi recuperado e a equipe adotou uma estratégia mais cuidadosa de commits e branches a partir desse ponto.

---

## 7. Conclusão

O desenvolvimento do Sistema de Biblioteca permitiu à dupla aplicar na prática os conceitos de arquitetura de microsserviços, conteinerização com Docker e desenvolvimento web com Spring Boot. 

A separação de responsabilidades entre os serviços ficou clara durante o desenvolvimento: cada microsserviço pode ser construído, testado e implantado de forma independente. O `docker compose up --build` demonstra isso ao orquestrar toda a infraestrutura com um único comando, tornando o sistema reproduzível em qualquer máquina que tenha o Docker instalado.

As dificuldades encontradas, especialmente com o Docker, foram superadas com pesquisa e análise dos logs de erro, consolidando o aprendizado sobre infraestrutura de aplicações modernas.

---

## 8. Referências

- Documentação oficial do Spring Boot: https://docs.spring.io/spring-boot/
- Documentação do Docker Compose: https://docs.docker.com/compose/
- Documentação do Nginx: https://nginx.org/en/docs/
- Documentação do PostgreSQL: https://www.postgresql.org/docs/
- RICHARDSON, Chris. *Microservices Patterns*. Manning Publications, 2018.
