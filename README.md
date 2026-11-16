# Estacionamento Inteligente
### Sistema de Gerenciamento de Estacionamentos

**UniAnchieta — Centro Universitário Padre Anchieta**

| Informação | Detalhes |
|---|---|
| Disciplina | Programação Orientada a Objetos em Java |
| Projeto | Avaliação 2 (AV2) — Startup OO |
| Professor | Jesse Guimarães |
| Linguagem | Java 21 |
| Semestre | 2º semestre de 2026 |

## 1. Integrantes do grupo

- Felipe Peres
- [Nome do integrante 2]
- [Nome do integrante 3]
- [Nome do integrante 4]
- [Nome do integrante 5]
- [Nome do integrante 6]

## 2. Sobre o projeto

O **Estacionamento Inteligente** é um sistema desenvolvido em Java com o objetivo de gerenciar operações de estacionamentos, permitindo controlar vagas, veículos, tickets, tarifas, mensalistas e consumo de energia para veículos elétricos.

A aplicação permite cadastrar diferentes estacionamentos, cada um com seu próprio nome e quantidade de vagas disponíveis por categoria.

Antes de acessar o menu principal, o usuário deve selecionar um estacionamento previamente cadastrado. Todas as operações realizadas a partir desse momento ficam vinculadas ao estacionamento selecionado.

O projeto utiliza os conceitos de Programação Orientada a Objetos para representar as entidades do negócio, organizar suas responsabilidades e garantir que as regras sejam respeitadas durante a execução.

## 3. Objetivos

- Aplicar os conceitos fundamentais de Programação Orientada a Objetos em Java.
- Desenvolver um modelo de domínio baseado em um negócio real.
- Utilizar classes abstratas, interfaces, herança e composição.
- Garantir a integridade dos dados por meio de invariantes.
- Implementar ciclos de vida utilizando enums.
- Aplicar exceções personalizadas para situações específicas do negócio.
- Utilizar polimorfismo para permitir diferentes comportamentos.
- Implementar cálculos e relatórios utilizando recursos da linguagem Java.

## 4. Regras de negócio

O sistema deve respeitar as seguintes regras:

**RN01 — Compatibilidade entre veículos e vagas**

Existem vagas específicas para carros, motos, veículos elétricos e pessoas com deficiência (PcD). Um veículo somente poderá ocupar uma vaga compatível com sua categoria.

**RN02 — Ocupação exclusiva**

Uma vaga nunca poderá possuir dois veículos simultaneamente. Da mesma forma, um veículo não poderá ocupar duas vagas ao mesmo tempo.

**RN03 — Cálculo de tarifas**

O estacionamento oferece 15 minutos de tolerância. Após esse período, a cobrança é realizada por hora ou fração, respeitando um valor máximo de diária. Mensalistas com mensalidade em dia são isentos da tarifa de estacionamento.

**RN04 — Cobrança de recarga elétrica**

Veículos elétricos estacionados em vagas com recarga possuem uma cobrança adicional, calculada de acordo com a quantidade de energia consumida em kWh.

**RN05 — Ciclo de vida do ticket**

O ticket possui três estados possíveis:

`ABERTO → PAGO → ENCERRADO`

A saída do veículo somente poderá ser liberada pela cancela quando o ticket estiver no estado `PAGO`.

**RN06 — Validação de placas**

As placas dos veículos devem seguir um dos padrões brasileiros aceitos:

- Mercosul: ABC1D23.
- Modelo antigo: ABC1234.

**RN07 — Bloqueio de vagas para manutenção**

Vagas bloqueadas para manutenção não poderão receber veículos. Além disso, uma vaga ocupada não poderá ser bloqueada.

**RN08 — Situação da mensalidade**

A isenção da tarifa depende da situação financeira do mensalista. Caso a mensalidade esteja atrasada, o cliente deverá pagar a tarifa normal do estacionamento.

**RN09 — Gerenciamento de estacionamentos**

Cada estacionamento deve ser cadastrado com nome e quantidade de vagas por tipo. Antes de acessar o menu principal, o usuário deverá selecionar um estacionamento cadastrado. Todas as operações devem ocorrer dentro do estacionamento selecionado.

## 5. Requisitos técnicos da AV2

O desenvolvimento deve contemplar os 12 requisitos técnicos obrigatórios definidos no enunciado da avaliação.

| Código | Requisito | Descrição |
|---|---|---|
| R01 | Linguagem ubíqua | Glossário com pelo menos 8 termos do negócio, utilizando os mesmos nomes no código. |
| R02 | Regras rastreadas | Pelo menos 8 regras de negócio relacionadas às classes e métodos responsáveis por garanti-las. |
| R03 | Modelo rico | Pelo menos 8 tipos próprios além da classe Main, cada um em seu arquivo Java. |
| R04 | Classe abstrata | Uma classe abstrata com método abstrato e concreto, além de pelo menos duas subclasses. |
| R05 | Interfaces | Pelo menos duas interfaces próprias, com uma delas implementada por classes sem relação de herança entre si e utilizada polimorficamente. |
| R06 | Composição | Pelo menos uma relação de composição que dependa de uma interface recebida pelo construtor. |
| R07 | Ciclo de vida | Enum de estados com transições controladas por métodos com intenção. |
| R08 | Objeto de valor | Pelo menos um record ou classe final imutável, validado em seu próprio construtor. |
| R09 | Invariantes | Objetos sempre válidos, ausência de setters públicos para estados críticos e proteção das coleções internas com List.copyOf. |
| R10 | Exceções de domínio | Pelo menos duas exceções próprias, lançadas pelo modelo e tratadas com try-catch no menu. |
| R11 | Ordenação | Implementação de Comparable e pelo menos um Comparator, utilizados em listagens. |
| R12 | Aplicação | Menu com Scanner, pelo menos seis opções, dados de exemplo carregados na inicialização e Main responsável apenas pela orquestração. |

## 6. Desafios escolhidos — Trilha Vá Além

O grupo escolheu três desafios da Trilha Vá Além, conforme as possibilidades apresentadas no enunciado.

### C — Padrão Strategy

Implementar o padrão Strategy para permitir que uma regra de cálculo seja substituída em tempo de execução por meio de uma interface.

No projeto, o objetivo é permitir diferentes estratégias de cálculo de tarifas, mantendo o sistema flexível e facilitando a inclusão de novas políticas de cobrança.

### D — Relatórios com Streams

Desenvolver pelo menos três relatórios gerenciais utilizando Streams, lambdas e recursos da API de Collections do Java.

Os relatórios devem utilizar:

- filter
- map
- sorted
- Collectors.groupingBy

O objetivo é facilitar a análise das operações realizadas nos estacionamentos.

### F — Arquitetura em camadas

Organizar o sistema em três camadas principais:

- **dominio:** contém as entidades, regras de negócio, interfaces, enums, objetos de valor e exceções.
- **aplicacao:** concentra os serviços e a coordenação dos casos de uso.
- **app:** responsável pela interação com o usuário, menus, entrada de dados e inicialização da aplicação.

A camada de domínio não deve depender de Scanner nem realizar impressões diretamente no console.

## 7. Conceitos de POO aplicados

O projeto utiliza os seguintes conceitos:

- **Encapsulamento:** proteção dos atributos e controle de acesso aos estados dos objetos.
- **Abstração:** representação dos conceitos essenciais do estacionamento por meio de classes abstratas e interfaces.
- **Herança:** reutilização e especialização de comportamentos entre classes relacionadas.
- **Polimorfismo:** utilização de diferentes implementações por meio de contratos comuns.
- **Composição:** relacionamento entre objetos para organizar responsabilidades e comportamentos.
- **Imutabilidade:** utilização de objetos de valor para representar informações que não devem ser modificadas após sua criação.
- **Exceções de domínio:** representação de situações inválidas específicas das regras do negócio.

## 8. Tecnologias e ferramentas

- Java 21 (JDK 21).
- Programação Orientada a Objetos.
- IntelliJ IDEA.
- Git e GitHub.
- Java Collections Framework.
- Java Streams API.

## 9. Estrutura do projeto

O projeto segue a proposta de arquitetura em camadas:

```text
estacionamento-inteligente/
│
├── docs/
│   ├── glossario.md
│   ├── regras.md
│   ├── diagrama-classes.png
│   └── adr/
│       ├── 0001-decisao.md
│       └── 0002-decisao.md
│
├── src/
│   ├── dominio/
│   ├── aplicacao/
│   └── app/
│
├── README.md
└── estacionamento_inteligente.iml
```

*Os arquivos e diretórios devem refletir a estrutura efetivamente adotada na versão final do projeto.*

## 10. Documentação de engenharia

A documentação complementar do projeto deve conter:

- **Glossário:** definição dos principais termos utilizados no negócio.
- **Regras de negócio:** descrição das regras e matriz de rastreabilidade, relacionando cada regra à classe e ao método responsável por garanti-la.
- **Diagrama UML:** representação das classes, atributos, métodos, interfaces, enums, records, heranças e composições.
- **ADRs (Architecture Decision Records):** registros das principais decisões arquiteturais, incluindo contexto, decisão tomada e consequências.

## 11. Execução do projeto

**Requisitos:** JDK 21 instalado e configurado.

O projeto poderá ser executado por meio de uma IDE compatível com Java ou pelo terminal, após a compilação dos arquivos-fonte.

As instruções específicas de compilação e execução deverão ser atualizadas conforme a estrutura definitiva dos pacotes e arquivos do projeto.

## 12. Considerações finais

O Estacionamento Inteligente busca representar um sistema real de gerenciamento de estacionamentos, aplicando os princípios de Programação Orientada a Objetos e boas práticas de desenvolvimento de software.

Mais do que implementar funcionalidades, o objetivo é construir um modelo de domínio consistente, no qual as próprias classes sejam responsáveis por proteger as regras de negócio e impedir estados inválidos.

O projeto faz parte da avaliação AV2 da disciplina de Programação Orientada a Objetos em Java, ministrada pelo professor Jesse Guimarães, no segundo semestre de 2026, no UniAnchieta.
