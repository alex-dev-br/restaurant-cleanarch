## Arquitetura

A arquitura utilizada para o desenvolvimento do projeto foi a clean architecture (Arquitura Limpa),
nessa arquitura o código deve ser estruturado de forma que as regras de negócio permaneçam isoladas de detalhes técnicos, 
como bancos de dados, mensagerias, frameworks ou interfaces de usuário.
Neste projeto a separação foi feita pelo pacotes do java, a principal divisão está nos pacotes core e infra. 
O pacote core contém o código focado na regra de negocio, nele não foi utilizados classes de frameworks ou bibliotecas, com
exceção do lombok que ajuda a reduzir código boilerplate e que foi autorizado previamente o uso dentro do core.
O pacote de infra contém o classes com implementações das interfaces do core, que não tem interesse no como vai ser implementado,
implementações de acesso ao banco, segurança e interfaces de acesso para os usuários, que nesse caso é uma rest api.

### Core
O pacote core se segmenta nos seguintes pacotes, domain, exception, usecase, gateway, inbound, outbound, presenter e controller.
- Domain: Esse pacote as classes que representam as principais entidades do sistema como Restaurant (restaurante) 
e User (usuário). Essas classes possuem regras de validação que não permitem que um objeto delas seja criado de forma 
inválida, contém métodos que representam comportamentos entidade e não permitem manipulação dos seus atributos interno, 
um exemplo disso é que a maioria não possuem metodos setters, os atributos são final e em casos de listas (List) e 
conjutos (Set) quando buscados por um getter é retornado uma cópia assim, não permitindo que classes de fora manipulem 
seus objetos. Outro ponto importante dentro do domain é o subpacote roles, que contém enums nomes de roles que são utilizadas
no sistema para controlar acesso
- Exception: Esse pacote contém as classes de exceções de negocio, todas elas tem como super classe a BusinessException
- Usecase: As classes desse pacote que contém as regras da aplicação, elas orquestram as interações entras entidades.
- Gateway: Esse pacote contém apenas interfaces java, nele ficam os pontos de contato com a infraestrutura, as interfaces
possuem ações que dependem da implementação feita pela infraestrutura, mas não importando como isso é feito.
- Inbound: Esse pacote contém as classes para entradas de dados para o domain da aplicação, onde essas classes possuem 
todos os atributos necessário para execução da ação solicitada
- Outbound: Esse pacote contém as classes para saidas de dados para o domain da aplicação, essas classes já tem um 
tratamento para somente o necessário seja devolvido
- Presenter: Neste pacotes estão localizados as classes que fazem a coversão das classes de entrada de dados (Inbound) 
para classes de dominio (domain), e das classes de dominio (domain) para classes para de saida  de dados (Outbound).
- Controller: Esse pacote serve como orquestrador dos usescase, ele sabe a ordem que deve chamar, além de qual presenter
deve utilizado para o que foi requisitado.

### Infra
O pacote de infra contém as configurações do framework que no nosso caso é a stack do spring-boot e implementações das
interfaces do core. Os principais sub-pacotes dentro da infra são:
- Config: como o nome sugere esse pacote possuis as configurações do projeto, como criação de beans e configurações do 
spring-security
- Controller: no controller ficam abrigadas as classes de controle rest api do spring
- Mapper: o mapper contém as configurações de mapeado da biblioteca map struct
- Persistence: Neste contém as implementações dos gateweys de acesso e persistencia de dados, configuração do JPA(entidades),
configurações de repositories do spring-data-jpa.
