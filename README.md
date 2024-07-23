# Community Backend Microservices

O Community backend microservices é o backend da aplicação [Community](https://github.com/luizfelipeapolonio/community)
reescrito na arquitetura de microserviços, construído inteiramente com o **Java Spring Framework**. Se trata de 
uma plataforma de interatividade em um formato semelhante a um blog, onde os usuários podem compartilhar conhecimento e 
coisas do seu interesse, além de poder interagir na seção de comentários de cada post. <br />
Este backend é constituído por 5 aplicações independentes que comunicam entre si, sendo uma aplicação responsável por 
gerenciar todas as operações relacionadas ao usuário, uma para os posts, uma para o upload de imagens, um gateway para 
receber todas as requisições, e um server de **Service Discovery** para orquestrar todos os serviços.

<br />

<img src="./assets/Community_Microservices.png" alt="Imagem demonstrando o fluxo da aplicação" />

<br /><br />

<p align="center">
    <a href="#tech">Tecnologias utilizadas</a> •
    <a href="#resources">Funções/Recursos</a> •
    <a href="#endpoints">Rotas da API</a> •
    <a href="#run">Como rodar a aplicação</a> •
    <a href="#license">Licença</a> •
    <a href="#author">Autor</a>
</p>

<hr />

<h2 id="tech">💻 Tecnologias utilizadas</h2>

As ferramentas que foram utilizadas na construção do projeto:
- [Java 17](https://docs.oracle.com/en/java/javase/17/)
- [Spring Boot 3 (Spring 6)](https://spring.io/projects/spring-boot#overview)
- [Spring Security 6](https://docs.spring.io/spring-security/reference/index.html)
- [Spring Cloud Gateway](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
- [Spring Cloud Netflix Eureka](https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/)
- [Spring Cloud OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [Spring RestClient](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-restclient)
- [Maven](https://maven.apache.org/)
- [JPA + Hibernate](https://spring.io/projects/spring-data-jpa#overview)
- [Java Bean Validation](https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html#validation-beanvalidation-overview)
- [PostgreSQL](https://www.postgresql.org/)
- [JUnit5 + Mockito](https://docs.spring.io/spring-framework/reference/testing.html)
- [JWT (JSON Web Token)](https://github.com/auth0/java-jwt)
- [Docker](https://www.docker.com/)

<h2 id="resources">🚀 Funções/Recursos</h2>

- ### Community Service Discovery
  O Community Service Discovery é um servidor que registra e mapeia a instância de cada microsserviço feito com 
  **Spring Cloud Netflix Eureka**, com o objetivo de facilitar e simplificar a comunicação e fluxo entre os 
  microsserviços, além de ter o controle de quais microsserviços estão sendo ou não executados, e armazenar
  informações relevantes das instâncias de cada microsserviço, como o endereço de IP e porta, por exemplo.

- ### Community Api Gateway
  O Community Api Gateway é a aplicação responsável por ser o entrypoint da aplicação como um todo, sendo a porta 
  de entrada de todas as requisições, feito com o **Spring Cloud Gateway**. Principais recusros:
  - **Roteamento:** Recebe todas as requisições e redireciona para o microsserviço correspondente.
  - **Filtros:** Aplica um filtro de autenticação que protege/restringe o acesso às rotas dos microsserviços 
  baseados na autenticação do usuário.
  - **Autenticação:** Recebe o JSON Web Token do usuário, faz a chamada para o microsserviço responsável por 
  validar o token (Community User Service), e baseado na resposta, autoriza ou nega o acesso a rota requisitada.

- ### Community User Service
  O Community User Service é a aplicação responsável por realizar todas as operações de CRUD (Create, Read, Update,
  Delete) referentes ao usuário, além de ser responsável por validar e autenticar o usuário utilizando **JWT (JSON 
  Web Token)** e **Spring Security**. Principais recursos:
  - **Autenticação e Autorização:** Sistema de autenticação stateless baseado na criação e validação de JSON 
  Web Token (JWT) e proteção de rotas com Spring Security.
  - **Camadas:** Divisão da aplicação em 4 camadas principais (Padrão MVC): `Model`, `Repository`, `Service` e 
  `Controller`. Fazendo com que as reponsabilidades da aplicação fiquem bem definidas e separadas, melhorando as 
  possibilidades de escalonamento e manutenibilidade.
  - **Testes unitários:** Testes unitários das funções com o objetivo de assegurar que o código esteja implementado
  corretamente, seguindo as regras de negócio e requisitos funcionais da aplicação, promovendo assim, uma maior
  confiabilidade e manutenibilidade da aplicação.
  - **Tratamento de exceções:** Centralização do tratamento de todas as exceções da aplicação em um `Rest Controller Advice`.
  - **DTO(Data Transfer Objects):** Utilização de `Java Records` como DTOs para transferência de dados entre as
  requisições.
  - **Validação:** Validação dos dados das requisições com o Hibernate/Jakarta Validation.
  - **Armazenamento:** Armazenamento dos dados em um banco de dados Postgres executando em container Docker.

- ### Community Post Service
  O Community Post Service é a aplicação responsável por realizar todas as operações de CRUD (Create, Read, Update,
  Delete) referentes aos posts. Principais recursos:
  - **Camadas:** Divisão da aplicação em 4 camadas principais (Padrão MVC): `Model`, `Repository`, `Service` e
  `Controller`. Fazendo com que as reponsabilidades da aplicação fiquem bem definidas e separadas, melhorando as
  possibilidades de escalonamento e manutenibilidade.
  - **Testes unitários:** Testes unitários das funções com o objetivo de assegurar que o código esteja implementado
  corretamente, seguindo as regras de negócio e requisitos funcionais da aplicação, promovendo assim, uma maior
  confiabilidade e manutenibilidade da aplicação.
  - **Tratamento de exceções:** Centralização do tratamento de todas as exceções da aplicação em um `Rest Controller Advice`.
  - **DTO(Data Transfer Objects):** Utilização de `Java Records` como DTOs para transferência de dados entre as
  requisições.
  - **Validação:** Validação dos dados das requisições com o Hibernate/Jakarta Validation.
  - **Armazenamento:** Armazenamento dos dados em um banco de dados Postgres executando em container Docker.

- ### Community Upload Service
  O Community Upload Service é a aplicação responsável por fazer o upload e armazenamento das images vindas do 
  Community User Service e do Community Post Service. Principais recursos:
  - **Arquivos:** Utilização das APIs nativas **IO/NIO** de manipulação de arquivos do Java. As imagens são salvas 
    no diretório `uploads` na raíz da aplicação. Dentro do diretório `uploads` as imagens são encaminhadas para o 
    subdiretório `post` ou `user`, baseado em qual microsserviço fez a requisição.
  - **Armazenamento:** As informações de cada imagem são armazenadas em um banco de dados Postgres executado em um 
    container Docker.

<h2 id="endpoints">🧭 Rotas da API</h2>

### Auth

|     Tipo     | Rota             | Descrição                                           | Autenticação |
|:------------:|:-----------------|:----------------------------------------------------|:------------:|
| **_`POST`_** | `/auth/register` | Criar usuário [requisição/resposta](#auth-register) |     Não      |
| **_`POST`_** | `/auth/login`    | Logar usuário [requisição/resposta](#auth-login)    |     Não      |

<br />

### User

|      Tipo      | Rota              | Descrição                                                         | Autenticação | 
|:--------------:|:------------------|:------------------------------------------------------------------|:------------:|
|  **_`GET`_**   | `/users/me`       | Visualizar perfil do usuário autenticado [resposta](#auth-me)     |     Sim      | 
| **_`DELETE`_** | `/users/me`       | Excluir perfil do usuário autenticado [resposta](#auth-me-delete) |     Sim      |
|  **_`GET`_**   | `/users/{userId}` | Visualizar perfil de um usuário específico [resposta](#get-user)  |     Sim      |
| **_`PATCH`_**  | `/users/{userId}` | Atualizar dados do usuário [requisição/resposta](#update-user)    |     Sim      |

<br />

### Post

|      Tipo      | Rota                                         | Descrição                                                                                        | Autenticação | 
|:--------------:|:---------------------------------------------|:-------------------------------------------------------------------------------------------------|:------------:|
|  **_`POST`_**  | `/posts`                                     | Criar post [requisição/resposta](#create-post)                                                   |     Sim      | 
|  **_`GET`_**   | `/posts?q={query}&page={pageNumber}`         | Visualizar todos os posts [resposta](#get-posts)                                                 |     Sim      |
| **_`DELETE`_** | `/posts`                                     | Excluir todos os posts do usuário autenticado [resposta](#delete-posts)                          |     Sim      |
|  **_`GET`_**   | `/posts/liked?page={pageNumber}`             | Visualizar todos os posts marcados como "gostei" do usuário autenticado [resposta](#liked-posts) |     Sim      |
|  **_`GET`_**   | `/posts/{postId}`                            | Visualizar um post [resposta](#get-post)                                                         |     Sim      |
| **_`PATCH`_**  | `/posts/{postId}`                            | Atualizar um post [requisição/resposta](#update-post)                                            |     Sim      |
| **_`DELETE`_** | `/posts/{postId}`                            | Excluir um post [resposta](#delete-post)                                                         |     Sim      |
|  **_`GET`_**   | `/posts/users/{userId}?page={pageNumber}`    | Visualizar todos os posts de um usuário específico [resposta](#all-user-posts)                   |     Sim      |
|  **_`POST`_**  | `/posts/{postId}/comments`                   | Inserir um comentário em um post [requisição/resposta](#create-comment)                          |     Sim      |
|  **_`GET`_**   | `/posts/{postId}/comments?page={pageNumber}` | Visualizar todos os comentários de um post [resposta](#get-comments)                             |     Sim      |
| **_`PATCH`_**  | `/posts/{postId}/comments/{commentId}`       | Editar um comentário de um post [requisição/resposta](#edit-comment)                             |     Sim      |
| **_`DELETE`_** | `/posts/{postId}/comments/{commentId}`       | Excluir um comentário de um post [resposta](#delete-comment)                                     |     Sim      |
| **_`PATCH`_**  | `/posts/{postId}/like`                       | Dar/remover um like de um post [resposta](#give-like)                                            |     Sim      |
| **_`PATCH`_**  | `/posts/{postId}/dislike`                    | Dar/remover um dislike de um post [resposta](#give-dislike)                                      |     Sim      |

<br />

### Requisição e Resposta

**`AUTH`**

<h4 id="auth-register">POST /auth/register</h4>

**Requisição**
```json
{
  "name": "User 1",
  "email": "user1@email.com",
  "password": "123456"
}
```

**Resposta**
```json
{
  "status": "Success",
  "code": 201,
  "message": "Usuário criado com sucesso",
  "data": {
    "id": "a1e37ddd-395d-4a07-8544-1679a2344172",
    "name": "User 1",
    "email": "user1@email.com",
    "bio": null,
    "profileImage": null,
    "createdAt": "2024-06-30T19:17:04.60376",
    "updatedAt": "2024-06-30T19:17:04.60376"
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="auth-login">POST /auth/login</h4>

**Requisição**
```json
{
  "email": "user1@email.com",
  "password": "123456"
}
```

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Usuário logado",
  "data": {
    "userInfo": {
      "id": "a1e37ddd-395d-4a07-8544-1679a2344172",
      "name": "User 1",
      "email": "user1@email.com",
      "bio": "Desenvolvedor back-end Java/Spring Framework",
      "profileImage": "http://DESKTOP-85MKGC7:8080/images/uploads/user/1718749077763-campbell-3ZUsNJhi_Ik-unsplash.jpg",
      "createdAt": "2024-05-15T16:28:46.41",
      "updatedAt": "2024-06-18T19:18:02.8"
    },
    "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJjb21tdW5pdHktdXNlci1zZXJ2aWNlIiwic3Viwv36A"
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

**`USER`**

<h4 id="auth-me">GET /users/me</h4>

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Usuário autenticado",
  "data": {
    "id": "a1e37ddd-395d-4a07-8544-1679a2344172",
    "name": "User 1",
    "email": "user1@email.com",
    "bio": "Desenvolvedor back-end Java/Spring Framework",
    "profileImage": "http://DESKTOP-85MKGC7:8080/images/uploads/user/1718749077763-campbell-3ZUsNJhi_Ik-unsplash.jpg",
    "createdAt": "2024-05-15T16:28:46.41",
    "updatedAt": "2024-06-18T19:18:02.8"
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="auth-me-delete">DELETE /users/me</h4>

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Usuário excluído com sucesso",
  "data": {
    "deletedUser": {
      "id": "a1e37ddd-395d-4a07-8544-1679a2344172",
      "name": "User 1",
      "email": "user1@email.com",
      "bio": "Desenvolvedor back-end Java/Spring Framework",
      "profileImage": "http://DESKTOP-85MKGC7:8080/images/uploads/user/1718749077763-campbell-3ZUsNJhi_Ik-unsplash.jpg",
      "createdAt": "2024-06-29T19:23:20.67",
      "updatedAt": "2024-06-29T19:23:20.67"
    }
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="get-user">GET /users/{userId}</h4>

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Usuário encontrado",
  "data": {
    "id": "a1e37ddd-395d-4a07-8544-1679a2344172",
    "name": "User 1",
    "email": "user1@email.com",
    "bio": "Desenvolvedor back-end Java/Spring Framework",
    "profileImage": "http://DESKTOP-85MKGC7:8080/images/uploads/user/1718749077763-campbell-3ZUsNJhi_Ik-unsplash.jpg",
    "createdAt": "2024-06-30T19:17:04.6",
    "updatedAt": "2024-06-30T19:17:04.6"
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="update-user">PATCH /users/{userId}</h4>

**Requisição**
```json lines
{
  // Os dados são enviados via FormData
  // O campo "data" deve conter os dados do usuário que devem ser atualizados
  // O campo "image" deve conter o arquivo da imagem que será associado ao perfil do usuário
  "data": {
    "name": "User 1 updated",
    "password": "654321",
    "bio": "Desenvolvedor back-end Java/Spring Framework"
  },
  "image": "campbell-3ZUsNJhi_Ik-unsplash.jpg" // Arquivo de imagem .jpg ou .png
}
```

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Usuário atualizado com sucesso",
  "data": {
    "id": "a1e37ddd-395d-4a07-8544-1679a2344172",
    "name": "User 1 updated",
    "email": "user1@email.com",
    "bio": "Desenvolvedor back-end Java/Spring Framework",
    "profileImage": "http://DESKTOP-85MKGC7:8080/images/uploads/user/1718749077763-campbell-3ZUsNJhi_Ik-unsplash.jpg",
    "createdAt": "2024-05-15T16:28:46.41",
    "updatedAt": "2024-06-18T19:18:02.802105"
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

**`POST`**

<h4 id="create-post">POST /posts</h4>

**Requisição**
```json lines
{
  // Os dados são enviados como FormData
  // O campo "data" deve conter os dados do post
  // O campo "image" deve conter a imagem do post
  "data": {
    "title": "A great post",
    "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
    "tags": ["great", "post", "something"]
  },
  "image": "markus-spiske-iar-afB0QQw-unsplash.jpg"
}
```

**Resposta**
```json
{
  "status": "Success",
  "code": 201,
  "message": "Post criado com sucesso",
  "data": {
    "id": "c5703b92-7b17-4772-acd9-8c0a8a8e2a57",
    "title": "A great post",
    "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
    "ownerId": "a1e37ddd-395d-4a07-8544-1679a2344172",
    "tags": [
      "great",
      "post",
      "something"
    ],
    "postImage": "http://DESKTOP-85MKGC7:8080/images/uploads/post/1719785922088-markus-spiske-iar-afB0QQw-unsplash.jpg",
    "createdAt": "2024-06-30T19:18:43.237211",
    "updatedAt": "2024-06-30T19:18:43.237211"
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="get-posts">GET /posts?q={query}&page={pageNumber}</h4>

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Todos os posts que contém 'oop' no título ou tags",
  "data": {
    "posts": [
      {
        "id": "9da62c29-15cc-4485-8b6f-971b5de0f62a",
        "title": "Java development for web",
        "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        "ownerId": "3b4bff09-6c9c-4563-a3ef-217c9119caa5",
        "tags": [
          "java",
          "spring",
          "oop",
          "api"
        ],
        "postImage": "http://DESKTOP-85MKGC7:8080/images/uploads/post/1719019801918-luca-bravo-XJXWbfSo2f0-unsplash.jpg",
        "createdAt": "2024-06-21T22:30:05.31",
        "updatedAt": "2024-06-21T22:30:05.31"
      },
      {
        "id": "315b3c40-c324-42a6-adad-7fc82d69a2e3",
        "title": "A great post about oop",
        "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        "ownerId": "a1e37ddd-395d-4a07-8544-1679a2344172",
        "tags": [
          "great",
          "post"
        ],
        "postImage": "http://DESKTOP-85MKGC7:8080/images/uploads/post/1719785875568-markus-spiske-iar-afB0QQw-unsplash.jpg",
        "createdAt": "2024-06-30T19:17:57.57",
        "updatedAt": "2024-06-30T19:17:57.57"
      }
    ],
    "totalElements": 2,
    "totalPages": 1
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="delete-posts">DELETE /posts</h4>

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Todos os posts do usuário de id: 'c15e0d7b-ad41-4372-922e-d65a19e86a2b' foram excluídos com sucesso",
  "data": {
    "deletedPosts": [
      {
        "id": "2e7baa7b-ded6-400b-a7da-c642642658cc",
        "title": "Post to be deleted",
        "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        "ownerId": "c15e0d7b-ad41-4372-922e-d65a19e86a2b",
        "tags": [
          "delete",
          "it"
        ],
        "postImage": "http://DESKTOP-85MKGC7:8080/images/uploads/post/1718842297657-felix-lam-J7fxkhtOqt0-unsplash.jpg",
        "createdAt": "2024-06-19T21:11:38.69",
        "updatedAt": "2024-06-19T21:11:38.7"
      },
      {
        "id": "5e6d3da1-31da-4781-9345-383942edc8ba",
        "title": "Post to be deleted 2",
        "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        "ownerId": "c15e0d7b-ad41-4372-922e-d65a19e86a2b",
        "tags": [
          "delete",
          "it"
        ],
        "postImage": "http://DESKTOP-85MKGC7:8080/images/uploads/post/1718842321598-felix-lam-J7fxkhtOqt0-unsplash.jpg",
        "createdAt": "2024-06-19T21:12:02.44",
        "updatedAt": "2024-06-19T21:12:02.44"
      }
    ]
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="liked-posts">GET /posts/liked?page={pageNumber}</h4>

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Todos os posts marcados como 'gostei' do usuário de id: '3b4bff09-6c9c-4563-a3ef-217c9119caa5'",
  "data": {
    "posts": [
      {
        "id": "315b3c40-c324-42a6-adad-7fc82d69a2e3",
        "title": "A great post 1",
        "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        "ownerId": "a1e37ddd-395d-4a07-8544-1679a2344172",
        "tags": [
          "great",
          "post"
        ],
        "postImage": "http://DESKTOP-85MKGC7:8080/images/uploads/post/1719785875568-markus-spiske-iar-afB0QQw-unsplash.jpg",
        "createdAt": "2024-06-30T19:17:57.57",
        "updatedAt": "2024-06-30T19:17:57.57"
      },
      {
        "id": "edd454ca-b7cb-41dd-b196-eb1f8a747cea",
        "title": "A great post 2",
        "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        "ownerId": "a1e37ddd-395d-4a07-8544-1679a2344172",
        "tags": [
          "great",
          "post",
          "golang"
        ],
        "postImage": "http://DESKTOP-85MKGC7:8080/images/uploads/post/1719785905027-markus-spiske-iar-afB0QQw-unsplash.jpg",
        "createdAt": "2024-06-30T19:18:26.25",
        "updatedAt": "2024-06-30T19:18:26.25"
      }
    ],
    "totalElements": 2,
    "totalPages": 1
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="get-post">GET /posts/{postId}</h4>

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Post de id: '315b3c40-c324-42a6-adad-7fc82d69a2e3' encontrado",
  "data": {
    "post": {
      "id": "315b3c40-c324-42a6-adad-7fc82d69a2e3",
      "title": "A great post 1",
      "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
      "ownerId": "a1e37ddd-395d-4a07-8544-1679a2344172",
      "tags": [
        "great",
        "post"
      ],
      "postImage": "http://DESKTOP-85MKGC7:8080/images/uploads/post/1719785875568-markus-spiske-iar-afB0QQw-unsplash.jpg",
      "createdAt": "2024-06-30T19:17:57.57",
      "updatedAt": "2024-06-30T19:17:57.57"
    },
    "postComments": {
      "comments": [],
      "totalElements": 0,
      "totalPages": 0
    },
    "likeDislike": {
      "isLikedOrDisliked": true,
      "type": "like",
      "likes": 2,
      "dislikes": 0
    }
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="update-post">PATCH /posts/{postId}</h4>

**Requisição**
```json lines
{
  // Os dados são enviados como FormData
  // O campo "data" deve conter os dados do post
  // O campo "image" deve conter a imagem do post
  "data": {
    "title": "Java development for web updated",
    "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
    "tags": [
      "java",
      "spring",
      "oop",
      "web",
      "dev"
    ],
  },
  "image": "markus-spiske-iar-afB0QQw-unsplash.jpg"
}
```

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Post atualizado com sucesso",
  "data": {
    "id": "a8b21221-4fea-4e78-a158-5c60a7e027e9",
    "title": "Java development for web updated",
    "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
    "ownerId": "3b4bff09-6c9c-4563-a3ef-217c9119caa5",
    "tags": [
      "java",
      "spring",
      "oop",
      "web",
      "dev"
    ],
    "postImage": "http://DESKTOP-85MKGC7:8080/images/uploads/post/1718829589052-markus-spiske-iar-afB0QQw-unsplash.jpg",
    "createdAt": "2024-06-18T19:21:01.17",
    "updatedAt": "2024-06-19T17:45:31.978546"
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="delete-post">DELETE /posts/{postId}</h4>

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Post de id: 'cc4220bd-1142-4af5-9169-2a9d36a6d3fb' excluído com sucesso",
  "data": {
    "deletedPost": {
      "id": "cc4220bd-1142-4af5-9169-2a9d36a6d3fb",
      "title": "Post to be deleted",
      "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
      "ownerId": "3b4bff09-6c9c-4563-a3ef-217c9119caa5",
      "tags": [
        "delete",
        "it"
      ],
      "postImage": "http://DESKTOP-85MKGC7:8080/images/uploads/post/1718834798051-felix-lam-J7fxkhtOqt0-unsplash.jpg",
      "createdAt": "2024-06-19T19:06:39.9",
      "updatedAt": "2024-06-19T19:06:39.9"
    }
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="all-user-posts">GET /posts/users/{userId}</h4>

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Todos os posts do usuário de id: 'a1e37ddd-395d-4a07-8544-1679a2344172'",
  "data": {
    "posts": [
      {
        "id": "315b3c40-c324-42a6-adad-7fc82d69a2e3",
        "title": "A great post 1",
        "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        "ownerId": "a1e37ddd-395d-4a07-8544-1679a2344172",
        "tags": [
          "great",
          "post"
        ],
        "postImage": "http://DESKTOP-85MKGC7:8080/images/uploads/post/1719785875568-markus-spiske-iar-afB0QQw-unsplash.jpg",
        "createdAt": "2024-06-30T19:17:57.57",
        "updatedAt": "2024-06-30T19:17:57.57"
      },
      {
        "id": "edd454ca-b7cb-41dd-b196-eb1f8a747cea",
        "title": "A great post 2",
        "content": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        "ownerId": "a1e37ddd-395d-4a07-8544-1679a2344172",
        "tags": [
          "great",
          "post",
          "golang"
        ],
        "postImage": "http://DESKTOP-85MKGC7:8080/images/uploads/post/1719785905027-markus-spiske-iar-afB0QQw-unsplash.jpg",
        "createdAt": "2024-06-30T19:18:26.25",
        "updatedAt": "2024-06-30T19:18:26.25"
      }
    ],
    "totalElements": 2,
    "totalPages": 1
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="create-comment">POST /posts/{postId}/comments</h4>

**Requisição**
```json
{
  "content": "A great comment for sure"
}
```

**Resposta**
```json
{
  "status": "Success",
  "code": 201,
  "message": "Comentário inserido com sucesso no post de id: '7c4a9b5f-6569-485b-a5a7-c9a09bc19604'",
  "data": {
    "id": "f86ef78f-f11b-4c88-9f6e-80a3d6259fe9",
    "content": "A great comment for sure",
    "username": "User 1",
    "profileImage": "http://DESKTOP-85MKGC7:8080/images/uploads/user/1718749077763-campbell-3ZUsNJhi_Ik-unsplash.jpg",
    "userId": "3b4bff09-6c9c-4563-a3ef-217c9119caa5",
    "postId": "7c4a9b5f-6569-485b-a5a7-c9a09bc19604",
    "createdAt": "2024-06-24T21:42:39.186039",
    "updatedAt": "2024-06-24T21:42:39.186039"
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="get-comments">GET /posts/{postId}/comments</h4>

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Todos os comentários do post de id: '7c4a9b5f-6569-485b-a5a7-c9a09bc19604'",
  "data": {
    "comments": [
      {
        "id": "f86ef78f-f11b-4c88-9f6e-80a3d6259fe9",
        "content": "A great comment for sure",
        "username": "User 1",
        "profileImage": "http://DESKTOP-85MKGC7:8080/images/uploads/user/1718749077763-campbell-3ZUsNJhi_Ik-unsplash.jpg",
        "userId": "3b4bff09-6c9c-4563-a3ef-217c9119caa5",
        "postId": "7c4a9b5f-6569-485b-a5a7-c9a09bc19604",
        "createdAt": "2024-06-24T21:42:39.19",
        "updatedAt": "2024-06-24T21:42:39.19"
      }
    ],
    "totalElements": 1,
    "totalPages": 1
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="edit-comment">PATCH /posts/{postId}/comments/{commentId}</h4>

**Requisição**
```json
{
  "content": "Updated comment content"
}
```

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Comentário editado com sucesso",
  "data": {
    "id": "27a2487f-35e2-4c5e-ab60-509604abbb35",
    "content": "Updated comment content",
    "username": "User 1",
    "profileImage": "http://DESKTOP-85MKGC7:8080/images/uploads/user/1718749077763-campbell-3ZUsNJhi_Ik-unsplash.jpg",
    "userId": "3b4bff09-6c9c-4563-a3ef-217c9119caa5",
    "postId": "9da62c29-15cc-4485-8b6f-971b5de0f62a",
    "createdAt": "2024-06-21T22:42:19.82",
    "updatedAt": "2024-06-24T00:09:10.657846"
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="delete-comment">DELETE /posts/{postId}/comments/{commentId}</h4>

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Comentário excluído com sucesso",
  "data": {
    "deletedComment": {
      "id": "f86ef78f-f11b-4c88-9f6e-80a3d6259fe9",
      "content": "A great comment for sure",
      "username": "User 1",
      "profileImage": "http://DESKTOP-85MKGC7:8080/images/uploads/user/1718749077763-campbell-3ZUsNJhi_Ik-unsplash.jpg",
      "userId": "3b4bff09-6c9c-4563-a3ef-217c9119caa5",
      "postId": "7c4a9b5f-6569-485b-a5a7-c9a09bc19604",
      "createdAt": "2024-06-24T21:42:39.19",
      "updatedAt": "2024-06-24T21:42:39.19"
    }
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="give-like">PATCH /posts/{postId}/like</h4>

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Like inserido com sucesso",
  "data": {
    "id": 13,
    "type": "like",
    "postId": "315b3c40-c324-42a6-adad-7fc82d69a2e3",
    "userId": "a1e37ddd-395d-4a07-8544-1679a2344172",
    "givenAt": "2024-07-03T18:49:16.326815"
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<br />

<h4 id="give-dislike">PATCH /posts/{postId}/dislike</h4>

**Resposta**
```json
{
  "status": "Success",
  "code": 200,
  "message": "Dislike inserido com sucesso",
  "data": {
    "id": 12,
    "type": "dislike",
    "postId": "40c6d5a8-6e23-431a-9a33-7e6a81c0b4b7",
    "userId": "3b4bff09-6c9c-4563-a3ef-217c9119caa5",
    "givenAt": "2024-07-02T17:10:13.730866"
  }
}
```
[Voltar para as rotas ⬆](#endpoints)

<h2 id="run">⚙ Como rodar a aplicação</h2>
-> Para executar a aplicação é necessário ter instalado o [Docker](https://www.docker.com/) e 
o [Git](https://git-scm.com/).
- Primeiro, clone este repositório:

  ```bash
  $ git clone https://github.com/luizfelipeapolonio/community-backend-microservices
  ```
- Acesse a pasta da aplicação:
  ```bash
  $ cd community-backend-microservices
  ```
- Renomeie o arquivo `jwt.env.example` para `jwt.env`, e coloque o par de chaves RSA no arquivo:
  ```bash
  JWT_PUBLIC_KEY='-----BEGIN PUBLIC KEY-----
  RSA KEY HERE
  -----END PUBLIC KEY-----'

  JWT_PRIVATE_KEY='-----BEGIN PRIVATE KEY-----
  RSA KEY HERE
  -----END PRIVATE KEY-----'
  ```
- Agora, basta buildar e inicializar todos os containers com o comando:
  ```bash
  $ docker compose up -d
  ```
- Todos os microsserviços e banco de dados serão construídos e inicializados. A aplicação estará disponível no endereço
`http://localhost:8080`

<h2 id="license">📝 Licença</h2>

Este repositório está licenciado pela **MIT LICENSE**. Para mais informações, leia o arquivo [LICENSE](./LICENSE) contido neste repositório.

<h2 id="author">Autor</h2>

Linkedin: [acesse meu perfil](https://www.linkedin.com/in/luiz-felipe-salgado-31a969273/).

Feito com 💜 por luizfelipeapolonio
