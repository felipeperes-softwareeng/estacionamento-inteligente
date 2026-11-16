# Glossário da Linguagem Ubíqua — Estacionamento Inteligente (R01)

## 1. Objetivo

Este documento define a linguagem ubíqua utilizada no desenvolvimento do sistema **Estacionamento Inteligente**, projeto AV2 da disciplina de Programação Orientada a Objetos em Java, do Centro Universitário Padre Anchieta (UniAnchieta).

A linguagem ubíqua estabelece um vocabulário comum entre as regras de negócio, a documentação e o código-fonte, garantindo que os conceitos do estacionamento sejam representados por nomes claros, consistentes e relacionados ao domínio.

O glossário abrange tanto os elementos já implementados quanto os previstos na arquitetura do projeto. A presença de um elemento neste documento não significa necessariamente que sua implementação já esteja concluída.

---

## 2. Entidades e objetos de valor

| Termo do negócio | Significado acordado | Representação no código |
|---|---|---|
| **Estacionamento** | Unidade de estacionamento identificada por um nome, responsável por suas vagas, tickets, mensalistas e política de cobrança. | `Estacionamento` (planejado) |
| **Veículo** | Automóvel ou motocicleta identificado por uma placa e classificado conforme sua categoria. | `Veiculo` |
| **Placa** | Identificador de um veículo, válido nos padrões brasileiros Mercosul (ABC1D23) ou antigo (ABC1234). | `record Placa` |
| **Tipo de Veículo** | Categoria utilizada para verificar a compatibilidade com as vagas disponíveis. | `enum TipoVeiculo` |
| **Vaga** | Espaço identificado por um código, destinado à ocupação de um único veículo compatível. | `abstract class Vaga` |
| **Vaga de Carro** | Vaga destinada a veículos classificados como carro. | `VagaCarro` |
| **Vaga de Moto** | Vaga destinada a motocicletas. | `VagaMoto` |
| **Vaga de Carro Elétrico** | Vaga destinada a carros elétricos, com possibilidade de cobrança adicional pelo consumo de energia. | `VagaCarroEletrico` |
| **Vaga PcD** | Vaga reservada ao uso por veículos autorizados conforme as regras de acessibilidade. No modelo atual, a autorização é representada pela categoria `PCD`. | `VagaPcd`, `TipoVeiculo.PCD` |
| **Status da Vaga** | Estado operacional de uma vaga: `LIVRE`, `OCUPADA` ou `BLOQUEADA`. | `enum StatusVaga` |
| **Ticket** | Registro da permanência de um veículo em uma vaga, contendo identificador, veículo, vaga, horário de entrada, situação e informações de pagamento. | `Ticket` |
| **Status do Ticket** | Etapa do ciclo de vida do ticket: `ABERTO`, `PAGO` ou `ENCERRADO`. | `enum StatusTicket` |
| **Mensalista** | Cliente recorrente associado a uma placa e a uma mensalidade, podendo receber isenção da tarifa de permanência quando estiver em dia. | `Mensalista` (planejado) |

## 3. Operações e regras do estacionamento

| Termo do negócio | Significado acordado | Representação no código |
|---|---|---|
| **Compatibilidade de Vaga** | Regra que determina se determinado veículo pode ocupar uma vaga, considerando sua categoria. | `Vaga.aceita(Veiculo)` |
| **Ocupação de Vaga** | Operação que associa um veículo a uma vaga livre e compatível, alterando seu estado para `OCUPADA`. | `Vaga.ocupar(Veiculo)` |
| **Liberação de Vaga** | Operação que remove o veículo de uma vaga ocupada, tornando-a novamente livre. No fluxo definitivo, deverá ocorrer somente após a autorização de saída. | `Vaga.liberar()` |
| **Bloqueio para Manutenção** | Operação que torna uma vaga livre indisponível para ocupação enquanto estiver em manutenção. | `Vaga.bloquear()` |
| **Desbloqueio de Vaga** | Operação que retira uma vaga do estado de manutenção e a torna disponível novamente. | `Vaga.desbloquear()` |
| **Registro de Entrada** | Operação que verifica a disponibilidade e compatibilidade da vaga, impede ocupações duplicadas e gera um ticket de entrada para o veículo. | `Estacionamento.registrarEntrada(...)` (planejado) |
| **Pagamento do Ticket** | Registro do pagamento devido pela permanência e pelos serviços utilizados. Após a validação, o ticket passa de `ABERTO` para `PAGO`. | `Ticket.pagar(double)` e operação de cobrança em `Estacionamento` (planejada) |
| **Liberação da Cancela** | Autorização de saída de um veículo após confirmar que seu ticket está `PAGO`, encerrando o ticket e liberando a vaga correspondente. | `Estacionamento.liberarCancela(...)` e `Ticket.encerrar()` |
| **Encerramento do Ticket** | Transição definitiva de `PAGO` para `ENCERRADO`, impedindo que o ticket seja encerrado novamente. | `Ticket.encerrar()` |
| **Seleção de Estacionamento** | Escolha da unidade na qual serão realizadas as operações do sistema. | Fluxo de seleção nas camadas `app` e `aplicacao` (planejado) |
| **Ocupação Exclusiva** | Regra que impede dois veículos na mesma vaga e um mesmo veículo em duas vagas simultaneamente. | `Vaga.ocupar(Veiculo)` e validação em `Estacionamento` (planejada) |

## 4. Tarifas, mensalidades e recarga elétrica

| Termo do negócio | Significado acordado | Representação no código |
|---|---|---|
| **Política de Tarifa** | Contrato que define o cálculo do valor da permanência de um veículo, permitindo diferentes estratégias de cobrança. | `interface PoliticaDeTarifa` (planejada) |
| **Tarifa Padrão** | Estratégia de cobrança que considera 15 minutos de tolerância, horas ou frações e limite máximo de diária. | `TarifaPadrao` (planejada) |
| **Tarifa Promocional** | Estratégia alternativa de cobrança, utilizada conforme a política comercial do estacionamento. | `TarifaPromocional` (planejada) |
| **Tempo de Permanência** | Intervalo entre a entrada do veículo e o momento considerado para calcular a saída ou o pagamento. | `Ticket.getEntrada()` e cálculo de duração (planejado) |
| **Tolerância** | Período inicial de até 15 minutos de permanência sem cobrança da tarifa de estacionamento. | `PoliticaDeTarifa.calcularValor(...)` (planejado) |
| **Hora ou Fração** | Critério de cobrança pelo qual qualquer fração adicional de hora, após a tolerância, é contabilizada conforme a política de tarifa. | `TarifaPadrao.calcularValor(...)` (planejado) |
| **Teto de Diária** | Limite máximo de cobrança de permanência dentro de um período de diária, conforme a política adotada. | `TarifaPadrao` (planejada) |
| **Recarga Elétrica** | Serviço de fornecimento de energia para veículos elétricos, cobrado separadamente da permanência. | `VagaCarroEletrico.calcularCustoRecarga(double)` |
| **Consumo em kWh** | Quantidade de energia utilizada durante a recarga, empregada no cálculo do valor adicional. | Parâmetro `kwh` de `calcularCustoRecarga()` |
| **Mensalidade em Dia** | Situação financeira na qual o mensalista está regular e possui direito à isenção da tarifa de permanência. | `Mensalista.temIsencao()` (planejado) |
| **Mensalidade em Atraso** | Situação em que o mensalista perdeu temporariamente o direito à isenção e deve pagar a tarifa normal. | `Mensalista.registrarAtraso()` (planejado) |
| **Regularização de Mensalidade** | Operação que registra a regularização financeira do mensalista, restabelecendo seu direito à isenção. | `Mensalista.regularizarPagamento()` (planejado) |
| **Item Cobrável** | Elemento do domínio que possui um valor associado à cobrança, como um ticket ou uma mensalidade. | `interface Cobravel` (planejada) |

## 5. Exceções do domínio

As exceções representam tentativas de realizar operações que violam as regras de negócio. Seus nomes devem expressar claramente o motivo da recusa.

As exceções abaixo estão planejadas para a evolução do sistema e ainda precisam ser implementadas como classes próprias.

| Termo do negócio | Significado acordado | Representação prevista |
|---|---|---|
| **Vaga Ocupada** | Tentativa de ocupar uma vaga que já possui veículo ou de bloqueá-la enquanto está ocupada. | `VagaOcupadaException` |
| **Vaga Bloqueada** | Tentativa de ocupar uma vaga indisponível por manutenção. | `VagaBloqueadaException` |
| **Veículo Incompatível** | Tentativa de estacionar um veículo em vaga que não aceita sua categoria. | `VagaIncompativelException` |
| **Veículo Já Estacionado** | Tentativa de registrar nova entrada para um veículo que já possui ocupação ativa. | `VeiculoJaEstacionadoException` |
| **Ticket Não Pago** | Tentativa de liberar a saída de um veículo sem que o ticket correspondente esteja pago. | `TicketNaoPagoException` |
| **Transição Inválida do Ticket** | Tentativa de alterar o ticket para um estado não permitido pelo ciclo de vida. | `TransicaoEstadoTicketException` |
| **Placa Inválida** | Tentativa de cadastrar uma placa fora dos padrões aceitos. | `PlacaInvalidaException` |
| **Estacionamento Inválido** | Tentativa de cadastrar ou selecionar uma unidade com informações inválidas. | `EstacionamentoInvalidoException` |

## 6. Relatórios e operações gerenciais

| Termo do negócio | Significado acordado | Representação prevista |
|---|---|---|
| **Listagem de Vagas** | Consulta das vagas de um estacionamento, apresentando seus códigos, categorias e estados. | Listagem de `Vaga` com `Comparable` e `Comparator` |
| **Listagem de Tickets** | Consulta dos tickets registrados em um estacionamento, incluindo seus estados e informações de cobrança. | Coleção de `Ticket` |
| **Relatório de Ocupação** | Demonstrativo da utilização das vagas por categoria ou situação. | Relatório com Java Streams na camada `aplicacao` |
| **Relatório de Receita** | Demonstrativo dos valores cobrados pelos serviços do estacionamento. | Relatório com Java Streams na camada `aplicacao` |
| **Relatório de Tickets** | Análise dos tickets registrados, permitindo agrupamentos e ordenações gerenciais. | Relatório com Java Streams na camada `aplicacao` |

## 7. Convenções de nomenclatura

Para garantir a consistência da linguagem ubíqua, o projeto adota as seguintes convenções:

- As classes do domínio utilizam nomes diretamente associados ao negócio, como `Vaga`, `Veiculo`, `Ticket`, `Estacionamento` e `Mensalista`.
- Métodos que alteram estados expressam uma ação, como `ocupar()`, `liberar()`, `bloquear()`, `desbloquear()`, `pagar()` e `encerrar()`.
- Os estados são representados por enums, evitando valores livres e transições arbitrárias.
- As exceções personalizadas utilizam nomes que identificam a violação da regra de negócio.
- Classes abstratas e interfaces representam conceitos e contratos, evitando nomes técnicos genéricos sem significado no domínio.
- A classe `VagaCarroEletrico` é a denominação adotada para vagas destinadas a carros elétricos, substituindo a denominação anterior `VagaEletrica`.
- As implementações futuras devem seguir os nomes acordados neste documento. Caso uma decisão arquitetural altere um conceito, o código, o glossário, as regras e o diagrama devem permanecer consistentes.

## 8. Relação com os requisitos da AV2

Este glossário atende à exigência documental do **R01 — Linguagem Ubíqua**, apresentando mais de oito termos próprios do negócio e relacionando-os aos elementos do sistema.

Também serve como referência para o **R02 — Regras Rastreadas**, auxiliando a identificação das classes e dos métodos responsáveis pelas regras RN01 a RN09.

A conformidade definitiva dos requisitos depende de os elementos citados estarem efetivamente implementados e corretamente relacionados ao código-fonte entregue.

---

**Projeto:** Estacionamento Inteligente  
**Disciplina:** Programação Orientada a Objetos em Java  
**Instituição:** Centro Universitário Padre Anchieta — UniAnchieta  
**Professor:** Jesse Guimarães  
**Semestre:** 2º semestre de 2026