<div align="center">

# Projeto: restaurant-cleanarch

(Postech – ADJ – fase 2 – Tech Challenge)

</div>

---

## Sumário

- [1. Introdução](#1-introdução)
- [2. Arquitetura do Sistema (Clean Architecture)](#2-arquitetura-do-sistema-clean-architecture)
- [3. Descrição dos Endpoints da API](#3-descrição-dos-endpoints-da-api)
- [4. Configuração do Projeto](#4-configuração-do-projeto)
- [5. Qualidade do Código](#5-qualidade-do-código)
- [6. Collections para Teste](#6-collections-para-teste)
- [7. Repositório do Código](#7-repositório-do-código)
- [Notas](#notas)

---

## Equipe

| Nome                                             | RM       | E‑mail                  |
|--------------------------------------------------|----------|-------------------------|
| Alexandre Belisário Duarte Leite de Andrade      | RM367163 | alexbdla@gmail.com      |
| Kervin Sama Candido da Silva                     | RM367345 | kervincandido@gmail.com |

---

## 1. Introdução

### Descrição do problema

Na nossa região, um grupo de restaurantes decidiu contratar estudantes para construir um sistema de gestão para seus 
estabelecimentos. Essa decisão foi motivada pelo alto custo de sistemas individuais, o que levou os restaurantes a se 
unirem para desenvolver um sistema único e compartilhado. Esse sistema permitirá que os clientes escolham restaurantes 
com base na comida oferecida, em vez de se basearem na qualidade do sistema de gestão.

O objetivo é criar um sistema robusto que permita a todos os restaurantes gerenciar eficientemente suas operações, 
enquanto os clientes poderão consultar informações, deixar avaliações e fazer pedidos online. Devido à limitação de 
recursos financeiros, foi acordado que a entrega do sistema será realizada em fases, garantindo que cada etapa seja 
desenvolvida de forma cuidadosa e eficaz.

A fase 2 expande o escopo para incluir o cadastro e a gestão de tipos de usuário (dono de restaurante e cliente), o 
cadastro de restaurantes e a gestão dos itens do cardápio. Essa fase foca em aplicar práticas de desenvolvimento 
orientadas a código limpo e uma arquitetura em camadas que facilite a manutenção e a evolução.

### Objetivo do projeto

Desenvolver um backend robusto em **Java 21** com **Spring Boot** para gerenciar tipos de usuário, restaurantes e itens 
de cardápio, seguindo a **CleanArchitecture** e atendendo aos requisitos definidos na Tech Challenge. O projeto 
utiliza banco de dados PostgreSQL com migrações via **Flyway**, mapeamento de entidades com **JPA**, autenticação via 
**Spring Security** e mapeamento de DTOs com **MapStruct**. O código foi estruturado para garantir alta coesão, 
baixo acoplamento e facilidade de testes automatizados.


Este projeto prioriza a separação de responsabilidades, com foco em escalabilidade e manutenção. Futuras fases podem 
incluir integração com serviços externos (ex: pagamentos, notificações) e expansão para pedidos online.

---

## 2. Arquitetura do Sistema (Clean Architecture)

### Visão Geral
Este projeto adota a Clean Architecture, proposta por Robert C. Martin (Uncle Bob), para isolar a lógica de negócio das dependências externas. Isso promove testabilidade, independência de frameworks e facilidade de evolução. A estrutura é dividida em camadas concêntricas:

Domain (Entidades): Regras de negócio puras e invariantes (ex: validações em MenuItem para preço positivo e nome obrigatório).
Application (Use Cases): Orquestração de fluxos de negócio (ex: CreateRestaurantUseCase verifica unicidade de nomes e permissões).
Ports (Interfaces): Contratos para entrada/saída (inbound/outbound), garantindo inversão de dependência.
Adapters (Infraestrutura): Implementações concretas (ex: JPA para persistência, REST para API).

#### Benefícios:

Independência: O core (domínio e use cases) não depende de frameworks; pode ser testado isoladamente.
Flexibilidade: Fácil trocar banco de dados ou adicionar novos adapters (ex: gRPC em vez de REST).
Testabilidade: Use cases são unitários; adapters são testados em integração.

### Diagrama de Camadas

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
|      +-----+------+      +-------+-------+      +----------+         |
|      |  Controller|----->|    Gateway    |<-----| Presenter|         |
|      +-----+------+      +-------+-------+      +----+-----+         |
|            |                     ^                   |               |
|            v                     |                   v               |
|      +-----+------+      +-------+-------+      +----+-----+         |
|      |   UseCase  |----->|     Domain    |<-----| In/Out   |         |
|      +------------+      +---------------+      +----------+         |
|                                                                      |
+----------------------------------------------------------------------+
```

Como pode ser visto no diagrama acima, este projeto utiliza a **Clean Architecture** para separar as regras de negócio dos detalhes de implementação. A
estrutura é dividida em duas camadas principais: `core` e `infra`.

- **`core`**: Contém a lógica de negócio pura, sem dependências de frameworks externos.
- **`infra`**: Fornece as implementações técnicas, como acesso a banco de dados e a API REST, dependendo do `core`.
### Camada Core

O `core` é o coração da aplicação e é subdividido em:

- **`domain`**: Contém as entidades de negócio (`Restaurant`, `User`) e suas regras de validação. Garante que os objetos 
de negócio estejam sempre em um estado válido.
- **`gateway`**: Define as interfaces (contratos) para operações externas, como persistência de dados 
(ex: `SalvarRestauranteGateway`).
- **`usecase`**: Orquestra as regras de negócio da aplicação, utilizando o `domain` e os `gateways` para executar ações 
específicas (ex: `CadastrarRestauranteUseCase`).
- **`inbound` / `outbound`**: DTOs (Data Transfer Objects) que definem a fronteira de dados para os `usecases`.
- **`presenter`**: Interfaces responsáveis por converter objetos do `domain` para o formato de saída (`outbound`).
- **`controller`**: Ponto de entrada para o `core`. Recebe solicitações, chama os `usecases` apropriados e gerencia o 
fluxo de negócio.

### Camada Infra

A `infra` implementa as interfaces do `core` e lida com as tecnologias externas.

- **`config`**: Configurações do Spring Framework, como injeção de dependência (Beans) e segurança.
- **`controller`**: Controladores da API REST (Spring MVC). Recebem requisições HTTP e as delegam para o `controller` 
do `core`.
- **`mapper`**: Conversores (usando MapStruct) que transformam os DTOs da API nos DTOs do `core` e vice-versa.
- **`persistence`**: Implementação dos `gateways` de persistência. Contém as entidades JPA e os repositórios Spring Data
que interagem com o banco de dados.

### Testes

A estratégia de testes garante a qualidade em ambas as camadas:

- **Testes de Unidade**: Focam na camada `core` (`domain`, `usecases`), utilizando mocks (Mockito) para simular as 
dependências externas (gateways).
- **Testes de Integração**: Validam a camada `infra`, testando a integração com o banco de dados (`@DataJpaTest`) e a 
API REST (`@SpringBootTest`).

**Ferramentas:** JUnit, AssertJ, Mockito e Spring Boot Test.

### Visão geral da arquitetura

Para o desenvolvimento deste projeto, adotamos a **CleanArchitecture**, um padrão de design proposto por Robert C. 
Martin (Uncle Bob). O objetivo principal é isolar a lógica de negócio das dependências externas, promovendo 
testabilidade e facilidade de evolução. A estrutura geral é composta de quatro anéis concêntricos:

1. **Domínio** – contém entidades e objetos de valor, com invariantes e regras intrínsecas. Por exemplo, `MenuItem` 
garante que nome não seja vazio e preço seja positivo; `Restaurant` valida que o dono possui
permissão para ser proprietário.
2. **Casos de Uso (Application)** – implementa as regras de negócio. Casos de uso como `CreateRestaurantUseCase` 
verificam unicidade de nomes e permissões antes de persistir dados.
3. **Ports** – interfaces que definem contratos de entrada (inbound) e saída (outbound) entre aplicação e infraestrutura. 
Inbound ports são usados por controladores; outbound ports abstraem persistência, envio de e‑mail ou hashing de senhas.
4. **Infraestrutura** – implementa os ports com tecnologias concretas: controladores REST, adaptadores de persistência
(JPA), mapeadores MapStruct, configuração de segurança e Docker.


### Organização dos pacotes

O código está organizado da seguinte forma:

- **`core/domain`** – modelos de domínio (entidades e objetos de valor) e regras de negócio próprias.
- **`core/usecase`** – casos de uso (serviços da aplicação) que orquestram operações, aplicando validações e regras 
específicas.
- **`core/inbound` e `core/outbound`** – definem ports. Inbound ports representam contratos de entrada para controladores; 
outbound ports definem interfaces para persistência, hashing de senhas, recuperação de usuário logado, etc.
- **`core/controller`** – controladores de aplicação que coordenam casos de uso e apresentam respostas (presenters).
- **`infra/controller`** – adaptadores HTTP (REST) implementados com Spring MVC. Cada REST controller mapeia os requests 
para os ports de entrada e converte os outputs para DTOs.
- **`infra/persistence`** – adaptadores de persistência usando Spring Data JPA para implementar os ports de saída. 
Mappers convertem entre entidades JPA e objetos de domínio.
- **`infra/config`** – configuração de infraestrutura, como segurança com Spring Security, perfil de banco de dados e 
serialização JSON. A classe `SecurityConfig` permite acesso público apenas aos endpoints de listagem de restaurantes e 
menu, exigindo autenticação para as demais rotas.

### Fluxo Típico

1. Requisição REST → infra/controller → Mapper para inbound DTO.
2. Inbound → core/controller → Valida acesso (via LoggedUserGateway).
3. Chama use case → Validações de negócio + interage com gateways.
4. Persistência via infra/persistence adapters.
5. Resposta: Presenter converte domínio para outbound DTO → Mapper para response REST.

## 3. Tecnologias utilizadas

- **Linguagem**: Java 21 (com records para DTOs e imutabilidade).
- **Framework**: Spring Boot 3.x (MVC, Data JPA, Security).
- **Banco de Dados**: PostgreSQL (dev/prod), H2 (testes).
- **Migração**: Flyway.
- **Mapeamento**: MapStruct (DTOs e entities).
- **Autenticação**: Spring Security (HTTP Basic, roles-based).
- **Testes**: JUnit 5, Mockito, AssertJ, Spring Boot Test (cobertura >80%).
- **Build**: Maven (com plugins Jacoco, Surefire).
- **Containerização**: Docker (multi-stage build) + Docker Compose.
- **Documentação**: SpringDoc OpenAPI (Swagger UI).
- **Outros**: Lombok (para getters/setters em entities), BCrypt (hashing de senhas).

### Banco de dados

Utilizamos **PostgreSQL** como banco de dados principal, com scripts de migração gerenciados pelo **Flyway**.
Para testes de integração, o perfil `test` usa H2 in‑memory. O Docker Compose define um contêiner PostgreSQL com volume 
persistente e um contêiner para a aplicação. As variáveis de ambiente e credenciais são configuradas via `.env`.


---

## 4. Descrição dos Endpoints da API

O backend expõe uma API REST organizada sob `/restaurants`, `/user-types`, `/user`, `/roles` e 
`/restaurants/{restaurant-id}/menu`. A autenticação padrão é **HTTP Basic**, e cada rota exige papéis definidos na 
classe de configuração de segurança.

### Tabela de Endpoints

| Path                                   | Métodos                     | Segurança      | Descrição                                                                                 |
|----------------------------------------|-----------------------------|----------------|-------------------------------------------------------------------------------------------|
| **/restaurants**                       | GET                        | Público        | Lista restaurantes com paginação e permite filtrar pelo tipo de cozinha.                  |
|                                        | POST                       | Autenticado    | Cria um novo restaurante. É necessário papel de criação de restaurante.                   |
|                                        | PUT                        | Autenticado    | Atualiza dados de um restaurante existente.                                               |
| **/restaurants/{id}**                  | GET                        | Público        | Retorna dados públicos de um restaurante pelo ID.                                         |
|                                        | DELETE                     | Autenticado    | Exclui um restaurante (somente para usuários autorizados).                                |
| **/restaurants/{id}/management**       | GET                        | Autenticado    | Retorna detalhes de gestão do restaurante, incluindo equipe, horários e cardápio.         |
| **/user-types**                        | GET                        | Com permissão  | Lista todos os tipos de usuário.                                                          |
|                                        | POST                       | Com permissão  | Cria um novo tipo de usuário.                                                             |
| **/user-types/{id}**                   | GET                        | Com permissão  | Consulta um tipo de usuário pelo ID.                                                      |
|                                        | PUT                        | Com permissão  | Atualiza um tipo de usuário existente.                                                    |
|                                        | DELETE                     | Com permissão  | Exclui um tipo de usuário.                                                                |
| **/user**                              | GET                        | Com permissão  | Lista usuários com paginação.                                                             |
|                                        | POST                       | Com permissão  | Cria um novo usuário.                                                                     |
| **/user/{id}**                         | GET                        | Com permissão  | Consulta usuário pelo UUID.                                                               |
|                                        | PUT                        | Com permissão  | Atualiza dados de um usuário existente.                                                   |
|                                        | DELETE                     | Com permissão  | Remove um usuário.                                                                        |
| **/roles**                             | GET                        | Com permissão  | Lista todos os papéis/roles existentes. Não há endpoints para criar ou atualizar roles.   |
| **/restaurants/{id}/menu**             | GET                        | Público        | Lista itens de menu de um restaurante com paginação.                                      |

> **Observação:** os casos de uso para **criar, atualizar e excluir itens de cardápio** existem no núcleo da aplicação, 
> mas ainda não estão expostos por rotas REST. Esses endpoints serão adicionados ⚠️⚠️⚠️Em elaboração⚠️⚠️⚠️.

### Documentação & Saúde

| Método | Path/URL                 | Descrição                                  |
|:-----:|--------------------------|---------------------------------------------|
| GET   | `/v3/api-docs`           | Documento OpenAPI gerado automaticamente.   |
| GET   | `/swagger-ui/index.html` | UI do Swagger para explorar a API.          |
| GET   | `/actuator/health`       | Healthcheck da aplicação.                   |

---

## 5. Configuração do Projeto (⚠️⚠️⚠️Em elaboração⚠️⚠️⚠️)

### 5.1 Pré-requisitos
- Java 21 JDK.
- Maven 3.8+.
- Docker & Docker Compose (para containerização).
- PostgreSQL (opcional para dev local).

### 5.1 Arquivos de Configuração

- **`.env.example`** – arquivo de exemplo com variáveis de ambiente; copie para `infra/.env` e ajuste os valores 
conforme sua máquina. Campos como `DB_NAME`, `DB_USER`, `DB_PASSWORD` e `JWT_SECURITY_TOKEN` devem ser definidos.
- **`infra/docker-compose.yaml`** – orquestra os contêineres do PostgreSQL e da aplicação. O serviço `postgres` monta 
um volume persistente, configura usuário, senha e banco de dados, e define *healthcheck*. O serviço `app` executa a 
aplicação com variáveis como `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_PROFILES_ACTIVE` e `JWT_SECURITY_TOKEN`【186017117635155†L26-L50】.
- **`infra/Dockerfile`** – build multi-stage (Maven + JRE 21) para compilar e empacotar a aplicação.

### 5.2 Perfis

- **stack** – sobe banco de dados e aplicação no Docker.
- **ide** – sobe apenas o banco de dados no Docker; a aplicação pode ser executada localmente pela IDE.

### 5.3 Como executar (⚠️⚠️⚠️Em elaboração⚠️⚠️⚠️)

1. Clone o repo: git clone https://github.com/alex-dev-br/restaurant-cleanarch.
2. Configure `.env`: Copie de .`infra/env.example` e ajuste (ex: `DB_PASSWORD`, `JWT_SECURITY_TOKEN`).`.
3. Build: `mvn clean package`.
4. Docker: `cd infra && docker compose --profile stack up -d --build`.
5. Local (sem Docker): `mvn spring-boot:run -Dspring-boot.run.profiles=dev`.

- Na raiz do projeto, execute:

```bash
cd infra
# Para subir toda a stack (app + postgres)
docker compose --profile stack up -d --build
# Para subir apenas o banco de dados (perfil ide)
docker compose --profile ide up -d
```

- Verifique se a aplicação está rodando acessando `http://localhost:8080/actuator/health`.

### 5.4 URLs úteis (perfis locais)

- **API**: `http://localhost:8080`
- **Health**: `http://localhost:8080/actuator/health`
- **Swagger**: `http://localhost:8080/swagger-ui/index.html`

---

## 6. Qualidade do Código

O projeto adota práticas de código limpo e princípios de engenharia de software:

- **Clean Architecture** e **SOLID** – separação de domínios, casos de uso, ports e adapters. A lógica de negócio 
reside em classes de domínio e casos de uso; a infraestrutura injeta implementações concretas por meio de interfaces.
- **Validações de domínio** – entidades como `MenuItem` e `Restaurant` possuem invariantes que impedem estados inválidos 
(por exemplo, preço > 0 e nome obrigatório).
- **Casos de uso coesos** – classes como `CreateRestaurantUseCase` validam permissões, unicidade de nomes e persistem 
apenas após todas as regras serem atendidas.
- **Segurança configurada** – `SecurityConfig` define rotas públicas e protegidas, desabilita CSRF em ambientes não dev 
e utiliza autenticação HTTP Basic.
- **Persistência limpa** – adaptadores JPA convertem entre entidades e objetos de domínio, e usam `ExampleMatcher` para 
checar duplicidades.
- **Testes automatizados** – o projeto inclui testes unitários e de integração cobrindo mais de 80% dos casos de uso. 
Os testes verificam cenários de sucesso e de falha (campos obrigatórios, permissões, nomes duplicados, etc.).
- **Plugins Maven** – Jacoco para cobertura de testes e Surefire para execução, configurados no `pom.xml`.
- **Docker** – contêineres multi‑stage geram imagens enxutas e performáticas; healthchecks garantem que os serviços só 
iniciem quando seus dependentes estiverem prontos.

**Métricas de Qualidade do Projeto:**  
O badge de CI indica o status do build e testes automatizados via GitHub Actions, enquanto o de cobertura mostra o 
percentual de código testado via Jacoco e Codecov.

[![Codecov Coverage](https://codecov.io/gh/alex-dev-br/restaurant-cleanarch/graph/badge.svg)](https://codecov.io/gh/alex-dev-br/restaurant-cleanarch)
[![CI Build](https://github.com/alex-dev-br/restaurant-cleanarch/actions/workflows/ci.yml/badge.svg)](https://github.com/alex-dev-br/restaurant-cleanarch/actions/workflows/ci.yml)
[![Codecov Coverage Graph](https://codecov.io/gh/alex-dev-br/restaurant-cleanarch/graphs/sunburst.svg?token=SEU_TOKEN)](https://codecov.io/gh/alex-dev-br/restaurant-cleanarch)

---

## 7. Collections para Teste

⚠️⚠️⚠️Em elaboração⚠️⚠️⚠️



---

## 8. Repositório do Código

### URL do Repositório

[GitHub](https://github.com/alex-dev-br/restaurant-cleanarch)

Este repositório contém todo o código fonte, incluindo as camadas de domínio, casos de uso, adaptadores, configurações e scripts de banco de dados. Os branches e releases podem ser acompanhados para verificar a evolução da fase 2 e futuras fases.

---

## Notas

- A especificação da API é gerada automaticamente pelo SpringDoc e pode ser consultada em `/v3/api-docs`. Em caso de divergência, a OpenAPI é a fonte da verdade para contratos e esquemas.
- Para executar o projeto em produção, ajuste as variáveis sensíveis (`DB_PASSWORD`, `JWT_SECURITY_TOKEN`) e utilize perfis adequados (`prod`).
- Esse README é uma documentação viva e pode sofrer ajustes conforme novas funcionalidades sejam implementadas (por exemplo, endpoints de criação e edição de itens do cardápio).

