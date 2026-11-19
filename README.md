# Estacionamento Inteligente

### Sistema de Gerenciamento de Estacionamentos — Projeto AV2

**UniAnchieta — Centro Universitário Padre Anchieta**

| Informação | Detalhes |
|---|---|
| Disciplina | Programação Orientada a Objetos em Java |
| Avaliação | AV2 — Startup OO |
| Professor | Jesse Guimarães |
| Linguagem | Java 25 (JDK 25) |
| Semestre | 2º semestre de 2026 |
| Repositório | [felipeperes-softwareeng/estacionamento-inteligente](https://github.com/felipeperes-softwareeng/estacionamento-inteligente) |

## 1. Integrantes do grupo

| Integrante | RA |
|---|---|
| Felipe Peres d'Oliveira | 2610386 |
| Danielle Ribeiro Castilho | 2642587 |
| Eduardo Necchio | 2627935 |
| Kauan Alexandre Gomes da Silva | 2627796 |
| Karina Evangelista Ferreira de Souza | 2639753 |
| Naiara Alves Dourado | 2638121 |

## 2. Apresentação do projeto

O **Estacionamento Inteligente** é uma aplicação de terminal desenvolvida em Java 21 para administrar unidades de estacionamento, vagas, veículos, tickets de entrada e saída, pagamentos, mensalistas e recargas de veículos elétricos.

O sistema aplica regras de negócio diretamente nas classes do domínio, evitando, por exemplo, a ocupação de uma vaga incompatível, a entrada simultânea da mesma placa em duas vagas, o pagamento duplicado de um ticket e a liberação da cancela antes da confirmação do pagamento.

A aplicação suporta o cadastro de **múltiplas unidades**, com seleção do estacionamento que será operado. As unidades cadastradas no mesmo `CadastroEstacionamentos` compartilham o controle de placas presentes.

**Escopo:** aplicação de console com dados mantidos **em memória**. Não utiliza banco de dados, interface gráfica ou integração com serviços reais de pagamento e cancelas. Ao encerrar a execução, as alterações realizadas pelo usuário não são persistidas.

## 3. Funcionalidades implementadas

- Cadastro e seleção de estacionamentos com quantidades configuráveis de vagas para **carros, motos, elétricos e PcD**.
- Registro de entrada, geração de ticket e identificação do veículo pela placa.
- Verificação de compatibilidade entre veículo e vaga e prevenção de ocupação duplicada.
- Validação de placas brasileiras nos padrões **ABC1234** e **ABC1D23**.
- Cálculo de tarifa com **15 minutos de tolerância**, cobrança por hora ou fração e teto por 24 horas.
- Alteração da política de cálculo entre **tarifa padrão** e **tarifa promocional**.
- Cobrança adicional de energia elétrica de acordo com o consumo em **kWh**.
- Cadastro, consulta, registro de atraso e regularização de mensalistas; isenção de permanência para mensalistas em dia.
- Pagamento exato do valor devido, sem cálculo de troco, e controle dos estados `ABERTO → PAGO → ENCERRADO`.
- Liberação de cancela somente após pagamento, com desocupação da vaga e preservação do histórico de tickets.
- Bloqueio e desbloqueio de vagas livres para manutenção.
- Listagem de vagas por código ou por situação e listagem de tickets.
- Relatórios gerenciais de **ocupação**, **receita de tickets** e **tickets por situação**.
- Listagem polimórfica de cobranças com a interface `Cobravel`.

## 4. Regras de negócio

As regras **RN01–RN06** correspondem ao domínio D3 — Mobilidade do enunciado. **RN07–RN09** são regras adicionais documentadas pelo grupo.

| Regra | Descrição | Implementação principal |
|---|---|---|
| RN01 | Somente veículos compatíveis podem ocupar cada tipo de vaga. | `Vaga.ocupar()`, `Vaga.aceita()` e subclasses |
| RN02 | Uma vaga não recebe dois veículos; a mesma placa não ocupa duas vagas ou unidades simultaneamente. | `Vaga.ocupar()`, `ControleDeOcupacao.registrarEntrada()`, `Estacionamento.registrarEntrada()` |
| RN03 | Até 15 min gratuitos; depois, cobrança por hora/fração e teto de diária; mensalista em dia tem isenção de permanência. | `TarifaPadrao.calcularValor()`, `Estacionamento.calcularCobranca()` |
| RN04 | Recarga elétrica cobrada separadamente por kWh consumido. | `VagaCarroEletrico.calcularCustoRecarga()` |
| RN05 | Ticket segue `ABERTO → PAGO → ENCERRADO`; cancela exige ticket pago. | `Ticket.pagar()`, `Ticket.encerrar()`, `Estacionamento.liberarCancela()` |
| RN06 | Apenas placas nos formatos brasileiro antigo e Mercosul são aceitas. | `Placa` (`record`) |
| RN07* | Vaga bloqueada não recebe veículo; vaga ocupada não pode ser bloqueada. | `Vaga.bloquear()`, `Vaga.desbloquear()`, `Vaga.ocupar()` |
| RN08* | Mensalista em atraso perde a isenção; regularização a restabelece. | `Mensalista`, `Estacionamento.calcularCobranca()` |
| RN09* | Unidade deve ser cadastrada e selecionada antes de suas operações. | `CadastroEstacionamentos`, `MenuConsole` |

A matriz detalhada de rastreabilidade, incluindo métodos e exceções associadas, está em [`docs/regras.md`](docs/regras.md).

## 5. Requisitos técnicos da AV2 — R01 a R12

| Código | Requisito | Evidência no projeto |
|---|---|---|
| R01 | Linguagem ubíqua | [`docs/glossario.md`](docs/glossario.md), com termos ligados às classes e aos métodos do negócio. |
| R02 | Regras rastreadas | [`docs/regras.md`](docs/regras.md), com nove regras RN01–RN09. |
| R03 | Modelo rico | Tipos próprios organizados em arquivos individuais em `src/dominio/` e `src/aplicacao/`. |
| R04 | Classe abstrata | `Vaga` (abstrata), método abstrato `aceita()`, método concreto `ocupar()` e quatro subclasses. |
| R05 | Interfaces | `PoliticaDeTarifa` e `Cobravel`; `Ticket` e `Mensalista` usados em `List<Cobravel>`. |
| R06 | Composição via interface | `Estacionamento` recebe `PoliticaDeTarifa` pelo construtor. |
| R07 | Ciclo de vida | `StatusTicket`, `Ticket.pagar()` e `Ticket.encerrar()`, com validação das transições. |
| R08 | Objeto de valor | `Placa` é um `record` imutável com validação no construtor. |
| R09 | Invariantes | Construtores validados, estado crítico sem setters públicos e listas retornadas com `List.copyOf()`. |
| R10 | Exceções de domínio | `PlacaInvalidaException`, `VagaOcupadaException`, `TicketNaoPagoException` e outras, tratadas em `MenuConsole.executar()`. |
| R11 | Ordenação | `Vaga implements Comparable<Vaga>` e `Comparator` em `RelatoriosGerenciais.listarVagasOrdenadas()`. |
| R12 | Aplicação de console | `Main`, `MenuConsole` com `Scanner`, mais de seis opções e dados iniciais de demonstração. |

## 6. Desafios escolhidos — Trilha Vá Além

Foram implementados **três desafios** entre os previstos no enunciado:

### Desafio C — Padrão Strategy

A interface `PoliticaDeTarifa` define o contrato de cálculo. `TarifaPadrao` e `TarifaPromocional` apresentam implementações diferentes, e `Estacionamento.alterarPoliticaDeTarifa()` permite trocá-las em execução. A opção **10** do menu demonstra essa mudança.

**Arquivos:** `src/dominio/PoliticaDeTarifa.java`, `TarifaPadrao.java`, `TarifaPromocional.java`, `Estacionamento.java`.

### Desafio D — Relatórios gerenciais com Streams

A classe `RelatoriosGerenciais` implementa três relatórios utilizando operações como `filter`, `map`, `sorted` e `Collectors.groupingBy`:

1. `ocupacaoPorTipo()` — número de vagas **atualmente ocupadas**, agrupadas por categoria.
2. `receitaPorTipo()` — valores registrados em tickets **PAGO** ou **ENCERRADO**, por categoria. As mensalidades contratuais não integram esse relatório.
3. `ticketsPorSituacao()` — quantidade de tickets em cada estado.

**Arquivo:** `src/aplicacao/RelatoriosGerenciais.java`. Os relatórios estão na opção **9** do menu.

### Desafio F — Arquitetura em camadas

O código foi separado em três pacotes:

- **`dominio`** — entidades, regras de negócio, interfaces, enums, objetos de valor e exceções.
- **`aplicacao`** — coordenação de operações, cadastro de unidades e relatórios.
- **`app`** — inicialização, leitura pelo `Scanner` e apresentação dos resultados no console.

A camada `dominio` não utiliza `Scanner` nem realiza impressão direta no console.

> **Testes automatizados:** o projeto também possui 41 verificações em Java puro, mas elas **não equivalem ao desafio A (JUnit 5)**, que exige testes com a biblioteca JUnit 5.

## 7. Estrutura do repositório

```text
estacionamento-inteligente/
├── src/
│   ├── dominio/                 # Entidades, vagas, tickets, tarifas, interfaces e exceções
│   ├── aplicacao/               # Cadastro de unidades, operações e relatórios
│   └── app/                     # Main e MenuConsole
├── verificacao/
│   └── VerificarRegras.java     # 41 verificações automatizadas em Java puro
├── docs/
│   ├── adr/
│   │   ├── 0001-vaga-como-classe-abstrata.md
│   │   ├── 0002-composicao-politica-de-tarifa.md
│   │   └── 0003-controle-compartilhado-de-ocupacao.md
│   ├── glossario.md
│   ├── regras.md
│   ├── verificacoes.md
│   ├── diagrama-classes.md
│   └── diagrama-classes.dot
├── .gitignore
├── estacionamento_inteligente.iml
└── README.md
```

O diagrama de classes está disponível em formato-fonte **Graphviz (`.dot`)** e em uma visão resumida em Markdown/Mermaid. Para gerar uma imagem PNG, com o Graphviz instalado, execute:

```bash
dot -Tpng docs/diagrama-classes.dot -o docs/diagrama-classes.png
```

A imagem `.png` deve ser gerada e incluída na entrega final se for solicitada pelo professor.

## 8. Como executar

### Requisitos

- JDK **21** instalado.
- IntelliJ IDEA **ou** terminal com os comandos `javac` e `java` disponíveis.
- Não é necessário instalar Maven, Gradle, banco de dados ou bibliotecas externas.

### Pelo IntelliJ IDEA

1. Abra a pasta raiz do repositório no IntelliJ.
2. Configure o **Project SDK** como **Java 21**.
3. Marque `src` como **Sources Root**, caso não esteja reconhecida.
4. Marque `verificacao` como **Test Sources Root** para executar o verificador pelo IDE.
5. Abra `src/app/Main.java` e execute `Main.main()` pelo botão **Run**.
6. Para executar os testes, abra `verificacao/VerificarRegras.java` e execute `VerificarRegras.main()`.

### Pelo terminal do Windows (PowerShell)

Na raiz do projeto:

```powershell
New-Item -ItemType Directory -Path out -Force | Out-Null
$arquivos = Get-ChildItem src,verificacao -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $arquivos
java -cp out app.Main
```

Para executar as verificações automatizadas após compilar:

```powershell
java -cp out verificacao.VerificarRegras
```

### Pelo terminal do Linux ou macOS

Na raiz do projeto:

```bash
mkdir -p out
find src verificacao -name '*.java' -print0 | xargs -0 javac -encoding UTF-8 -d out
java -cp out app.Main
```

Para executar as verificações automatizadas:

```bash
java -cp out verificacao.VerificarRegras
```

A saída esperada dos testes é:

```text
SUCESSO: 41 verificações passaram.
```

## 9. Dados de demonstração carregados na inicialização

O `Main.java` inicializa uma unidade chamada **Estacionamento do Jessé**, com a seguinte configuração:

| Categoria | Quantidade |
|---|---:|
| Vagas de carro | 5 |
| Vagas de moto | 3 |
| Vagas elétricas | 2 |
| Vagas PcD | 2 |
| **Total** | **12** |

A tarifa inicial é de **R$ 10,00 por hora**, com teto de **R$ 50,00 por 24 horas** e recarga elétrica de **R$ 3,00/kWh**.

Também são registrados **três mensalistas** e **seis tickets**. Após a inicialização, os tickets estão assim:

| Ticket | Veículo | Vaga | Situação |
|---|---|---|---|
| T001 | ABC1234 (carro) | C001 | ABERTO |
| T002 | DEF5678 (moto) | M001 | ABERTO |
| T003 | ELT1A23 (elétrico mensalista) | E001 | PAGO |
| T004 | GHI2B34 (PcD) | P001 | ABERTO |
| T005 | JKL9876 (carro mensalista em atraso) | C002 | ABERTO |
| T006 | PQR4D56 (elétrico) | E002 | ENCERRADO |

Assim, o cenário inicial possui **cinco vagas ocupadas**, com **quatro tickets abertos**, **um pago** e **um encerrado**. Os tickets T003 e T006 registram, respectivamente, **R$ 6,00** e **R$ 12,00** de recarga, totalizando **R$ 18,00** de receita de tickets elétricos no cenário inicial.

**Observação:** no código atual, a mensagem inicial informa incorretamente “2 mensalistas”; são **3 mensalistas** cadastrados. Recomenda-se corrigir apenas esse texto no `Main.java`.

## 10. Menu e roteiro básico de demonstração

### Menu inicial

| Opção | Ação |
|---|---|
| 1 | Cadastrar estacionamento |
| 2 | Selecionar estacionamento |
| 3 | Listar estacionamentos |
| 0 | Sair |

### Menu da unidade selecionada

| Opção | Ação |
|---|---|
| 1 | Registrar entrada |
| 2 | Consultar valor do ticket |
| 3 | Registrar pagamento |
| 4 | Liberar cancela / registrar saída |
| 5 | Bloquear ou desbloquear vaga |
| 6 | Gerenciar mensalistas |
| 7 | Listar vagas por código ou status |
| 8 | Listar tickets |
| 9 | Relatórios gerenciais |
| 10 | Trocar política de tarifa |
| 11 | Listar cobranças |
| 12 ou 0 | Voltar à seleção de estacionamentos |

**Sugestão para a demonstração:** execute `Main.main()`, selecione a opção **2**, informe **Estacionamento do Jessé**, consulte as vagas pela opção **7**, os tickets pela opção **8** e os três relatórios pela opção **9 → 4**. Depois use a opção **4** com o ticket **T003** para demonstrar que um pagamento confirmado permite liberar a cancela e desocupar a vaga elétrica.

Para demonstrar a regra dos 15 minutos sem esperar o tempo real, execute `VerificarRegras.main()`. O verificador injeta um `Clock` ajustável e simula 15, 16, 61 e mais minutos de permanência imediatamente.

## 11. Documentação e decisões arquiteturais

- [`docs/glossario.md`](docs/glossario.md) — linguagem ubíqua do domínio.
- [`docs/regras.md`](docs/regras.md) — nove regras e matriz de rastreabilidade.
- [`docs/adr/0001-vaga-como-classe-abstrata.md`](docs/adr/0001-vaga-como-classe-abstrata.md) — escolha da hierarquia abstrata de vagas.
- [`docs/adr/0002-composicao-politica-de-tarifa.md`](docs/adr/0002-composicao-politica-de-tarifa.md) — composição e Strategy para tarifa.
- [`docs/adr/0003-controle-compartilhado-de-ocupacao.md`](docs/adr/0003-controle-compartilhado-de-ocupacao.md) — prevenção de placas duplicadas entre unidades.
- [`docs/diagrama-classes.md`](docs/diagrama-classes.md) e [`docs/diagrama-classes.dot`](docs/diagrama-classes.dot) — estrutura de classes e relacionamentos.
- [`docs/verificacoes.md`](docs/verificacoes.md) — cobertura e execução do verificador.

## 12. Limitações e possíveis melhorias

O projeto tem finalidade acadêmica e opera em um único processo, com interação por terminal. Como melhorias futuras, podem ser considerados persistência em arquivo ou banco de dados, histórico financeiro de mensalidades, troco em pagamentos, interfaces gráficas, integração com cancelas físicas, testes JUnit 5 e controle de concorrência para múltiplos operadores.

## 13. Considerações finais

O **Estacionamento Inteligente** demonstra conceitos de Programação Orientada a Objetos, como abstração, herança, interfaces, composição, encapsulamento, polimorfismo, objetos de valor, enums e exceções de domínio. O principal objetivo é concentrar as regras nas próprias entidades e serviços do negócio, impedindo operações inválidas e mantendo o estado do sistema consistente.

A implementação contempla os requisitos **R01 a R12**, documenta **nove regras de negócio** e realiza os desafios **C (Strategy), D (Streams) e F (arquitetura em camadas)** da AV2.
