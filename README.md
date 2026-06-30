# Sistema de Biblioteca — Microsserviços com Spring Boot

Sistema de gerenciamento de biblioteca desenvolvido com arquitetura de microsserviços, utilizando Spring Boot, Thymeleaf, PostgreSQL, Docker e Nginx.

> Trabalho Final — Disciplina: Desenvolvimento Web com Java  
> Instituto Federal de Educação, Ciência e Tecnologia do Ceará (IFCE)  
> Curso: Ciência da Computação

---

## Arquitetura

```
[Browser]
    |
[nginx :80] — API Gateway
    |— /              → front-ms  :8080 (Thymeleaf)
    |— /api/livros    → livro-ms  :8081 (REST + PostgreSQL)
    |— /api/autores   → autor-ms  :8082 (REST + PostgreSQL)
```

### Serviços

| Serviço | Tecnologia | Responsabilidade |
|---|---|---|
| `nginx` | Nginx 1.25 | API Gateway — roteamento e ponto único de entrada |
| `front-ms` | Spring Boot + Thymeleaf | Interface web — consome os microsserviços via RestClient |
| `livro-ms` | Spring Boot + REST + JPA | CRUD de livros com banco PostgreSQL próprio |
| `autor-ms` | Spring Boot + REST + JPA | CRUD de autores com banco PostgreSQL próprio |
| `postgres-livros` | PostgreSQL 16 | Banco de dados exclusivo do livro-ms |
| `postgres-autores` | PostgreSQL 16 | Banco de dados exclusivo do autor-ms |

### Regra de ouro dos microsserviços

Cada serviço possui seu próprio banco de dados. O `livro-ms` armazena apenas o `autorId` como referência lógica — quem resolve o nome do autor é o `front-ms`, consultando o `autor-ms` via HTTP antes de renderizar a página.

---

## Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e em execução

---

## Como executar

### 1. Clone o repositório

```bash
git clone <url-do-repositorio>
cd microservices-library-api
```

### 2. Suba todos os containers

```bash
docker compose up --build
```

Aguarde todos os serviços iniciarem (cerca de 1-2 minutos). O sistema estará pronto quando os três serviços Spring Boot exibirem `Started DemoApplication` no log.

### 3. Acesse o sistema

Abra o navegador e acesse:

```
http://localhost/autores   — Gerenciar autores
http://localhost/livros    — Gerenciar livros
```

---

## Endpoints da API REST

### autor-ms (`/api/autores`)

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/autores` | Lista todos os autores |
| GET | `/api/autores/{id}` | Busca autor por ID |
| POST | `/api/autores` | Cadastra novo autor |
| PUT | `/api/autores/{id}` | Atualiza autor |
| DELETE | `/api/autores/{id}` | Remove autor |

### livro-ms (`/api/livros`)

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/livros` | Lista todos os livros |
| GET | `/api/livros?disponivel=true` | Lista livros disponíveis |
| GET | `/api/livros/{id}` | Busca livro por ID |
| POST | `/api/livros` | Cadastra novo livro |
| PUT | `/api/livros/{id}` | Atualiza livro |
| DELETE | `/api/livros/{id}` | Remove livro |

---

## Gerenciamento dos containers

### Parar os containers (mantém os dados)

```bash
docker compose down
```

### Reiniciar os containers

```bash
docker compose restart
```

### Remover containers e volumes (apaga todos os dados)

```bash
docker compose down -v
```

> O comando `down -v` remove permanentemente todos os dados cadastrados nos bancos de dados.

### Ver logs de um serviço específico

```bash
docker compose logs autor-ms
docker compose logs livro-ms
docker compose logs front-ms
```

---

## Persistência de dados

Os dados são persistidos em volumes Docker declarados no `docker-compose.yml`:

- `dados-autores` — banco de dados do autor-ms
- `dados-livros` — banco de dados do livro-ms

Os dados sobrevivem a `docker compose down` e `docker compose restart`. Apenas `docker compose down -v` remove os volumes.

---

## Estrutura do repositório

```
/
├── docker-compose.yml       # Orquestração dos containers
├── nginx.conf               # Configuração do API Gateway
├── README.md
├── autor-ms/
│   ├── Dockerfile
│   └── src/
├── livro-ms/
│   ├── Dockerfile
│   └── src/
└── front-ms/
    ├── Dockerfile
    └── src/
```

---

## Tecnologias utilizadas

- **Java 21**
- **Spring Boot 3.5**
- **Spring Data JPA** — persistência com PostgreSQL
- **Spring Web / RestClient** — comunicação entre microsserviços
- **Thymeleaf** — renderização de templates HTML
- **PostgreSQL 16** — banco de dados relacional
- **Docker / Docker Compose** — containerização e orquestração
- **Nginx 1.25** — API Gateway e proxy reverso
- **Maven** — gerenciamento de dependências e build
