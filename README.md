# Sistema de Biblioteca - Arquitetura de Microsserviços

Trabalho Final da disciplina de Desenvolvimento Web com Java do curso de Ciência da Computação (IFCE). O projeto consiste na refatoração de um sistema de cadastro de livros para uma arquitetura de microsserviços.

## 🏗️ Visão Geral da Arquitetura

A aplicação é orquestrada via Docker Compose e dividida em quatro containers principais:

* **API Gateway (Nginx):** Funciona como ponto único de entrada, realizando o roteamento e o tratamento de CORS centralizado. 
* **front-ms:** Interface web desenvolvida com Spring Boot e Thymeleaf. Consome as APIs de livros e autores via RestClient e renderiza as páginas HTML.
* **livro-ms:** Microsserviço independente (Spring Boot + REST + JPA) responsável pelo CRUD de Livros. Possui seu próprio banco de dados PostgreSQL (`postgres-livros`) e armazena apenas o `autorId` como referência lógica.
* **autor-ms:** Microsserviço independente (Spring Boot + REST + JPA) responsável pelo CRUD de Autores. Opera com seu banco de dados isolado (`postgres-autores`) e não possui dependência dos demais serviços.

## 📁 Estrutura do Projeto

```text
/
├── autor-ms/          # Código-fonte e Dockerfile do serviço de autores
├── front-ms/          # Código-fonte e Dockerfile da interface web
├── livro-ms/          # Código-fonte e Dockerfile do serviço de livros
├── nginx/             # Arquivo nginx.conf
├── docker-compose.yml # Orquestração dos containers
└── README.md
