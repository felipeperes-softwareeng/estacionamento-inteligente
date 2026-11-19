# Diagrama de classes — Estacionamento Inteligente

A imagem principal e legível em zoom está em **`docs/diagrama-classes.png`**. O arquivo `diagrama-classes.svg` permite ampliação sem perda e `diagrama-classes.dot` contém o diagrama completo em Graphviz, com atributos, métodos e relacionamentos.

### Visão resumida da estrutura

```mermaid
classDiagram
  class Vaga {
    <<abstract>>
    -String codigo
    -StatusVaga status
    +aceita(Veiculo) boolean
    +bloquear()
    +desbloquear()
    ~ocupar(Veiculo)
  }
  class VagaCarro
  class VagaMoto
  class VagaCarroEletrico
  class VagaPcd
  Vaga <|-- VagaCarro
  Vaga <|-- VagaMoto
  Vaga <|-- VagaCarroEletrico
  Vaga <|-- VagaPcd
  class PoliticaDeTarifa {
    <<interface>>
    +calcularValor(long) double
    +getDescricao() String
  }
  class TarifaPadrao
  class TarifaPromocional
  PoliticaDeTarifa <|.. TarifaPadrao
  PoliticaDeTarifa <|.. TarifaPromocional
  class Cobravel {
    <<interface>>
    +getValorCobrado() double
    +getDescricaoCobranca() String
  }
  class Ticket {
    -StatusTicket status
    -double valorPago
    ~pagar(...)
    ~encerrar()
  }
  class Mensalista {
    -boolean emDia
    +temIsencao() boolean
  }
  Cobravel <|.. Ticket
  Cobravel <|.. Mensalista
  class Estacionamento {
    -List~Vaga~ vagas
    -List~Ticket~ tickets
    -PoliticaDeTarifa politicaDeTarifa
    +registrarEntrada(...)
    +pagarTicket(...)
    +liberarCancela(...)
  }
  Estacionamento *-- Vaga
  Estacionamento *-- Ticket
  Estacionamento *-- Mensalista
  Estacionamento o-- PoliticaDeTarifa
  class CadastroEstacionamentos
  class ControleDeOcupacao
  class OperacoesEstacionamento
  class RelatoriosGerenciais
  class MenuConsole
  class Main
  CadastroEstacionamentos *-- Estacionamento
  CadastroEstacionamentos *-- ControleDeOcupacao
  OperacoesEstacionamento --> CadastroEstacionamentos
  RelatoriosGerenciais --> CadastroEstacionamentos
  MenuConsole --> OperacoesEstacionamento
  MenuConsole --> RelatoriosGerenciais
  Main --> MenuConsole
```

A imagem `.png` e a versão `.dot` apresentam também `Veiculo`, `Placa` (record), os enums e os vínculos de associação, realização e composição.
