# Arquitetura do Projeto

Este projeto utiliza a **Clean Architecture** para separar as regras de negócio dos detalhes de implementação. A estrutura é dividida em duas camadas principais: `core` e `infra`.

- **`core`**: Contém a lógica de negócio pura, sem dependências de frameworks externos.
- **`infra`**: Fornece as implementações técnicas, como acesso a banco de dados e a API REST, dependendo do `core`.

A comunicação entre as camadas ocorre por meio de **interfaces** (abstrações) definidas no `core` e implementadas na `infra`. Isso é conhecido como **Inversão de Dependência**.

## Diagrama de Camadas

```
+----------------------------------------------------------------------+
|                                 Infra                                |
|    +----------------+   +-----------------+   +-------------------+  |
|    |   Controller   |   |   Persistence   |   |      Config       |  |
|    |    (API REST)  |   | (JPA, Repos...) |   | (Spring, Sec...)  |  |
|    +-------+--------+   +--------+--------+   +-------------------+  |
+------------|---------------------|-----------------------------------+
             | (depende de)        | (implementa)
             v                     v
+------------|---------------------|-----------------------------------+
|                                 Core                                 |
|      +-----+------+      +-------+-------+      +---------+         |
|      |  Controller|----->|    Gateway    |<-----| Presenter|         |
|      +-----+------+      +-------+-------+      +----+----+         |
|            |                     ^                   |               |
|            v                     |                   v               |
|      +-----+------+      +-------+-------+      +----+----+         |
|      |   UseCase  |----->|     Domain    |<-----| In/Out   |         |
|      +------------+      +---------------+      +----------+         |
|                                                                      |
+----------------------------------------------------------------------+
```

## Camada Core

O `core` é o coração da aplicação e é subdividido em:

- **`domain`**: Contém as entidades de negócio (`Restaurant`, `User`) e suas regras de validação. Garante que os objetos de negócio estejam sempre em um estado válido.
- **`gateway`**: Define as interfaces (contratos) para operações externas, como persistência de dados (ex: `SalvarRestauranteGateway`).
- **`usecase`**: Orquestra as regras de negócio da aplicação, utilizando o `domain` e os `gateways` para executar ações específicas (ex: `CadastrarRestauranteUseCase`).
- **`inbound` / `outbound`**: DTOs (Data Transfer Objects) que definem a fronteira de dados para os `usecases`.
- **`presenter`**: Interfaces responsáveis por converter objetos do `domain` para o formato de saída (`outbound`).
- **`controller`**: Ponto de entrada para o `core`. Recebe solicitações, chama os `usecases` apropriados e gerencia o fluxo de negócio.

## Camada Infra

A `infra` implementa as interfaces do `core` e lida com as tecnologias externas.

- **`config`**: Configurações do Spring Framework, como injeção de dependência (Beans) e segurança.
- **`controller`**: Controladores da API REST (Spring MVC). Recebem requisições HTTP e as delegam para o `controller` do `core`.
- **`mapper`**: Conversores (usando MapStruct) que transformam os DTOs da API nos DTOs do `core` e vice-versa.
- **`persistence`**: Implementação dos `gateways` de persistência. Contém as entidades JPA e os repositórios Spring Data que interagem com o banco de dados.

## Testes

A estratégia de testes garante a qualidade em ambas as camadas:

- **Testes de Unidade**: Focam na camada `core` (`domain`, `usecases`), utilizando mocks (Mockito) para simular as dependências externas (gateways).
- **Testes de Integração**: Validam a camada `infra`, testando a integração com o banco de dados (`@DataJpaTest`) e a API REST (`@SpringBootTest`).

**Ferramentas:** JUnit, AssertJ, Mockito e Spring Boot Test.