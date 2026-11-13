# Regras de Negócio e Matriz de Rastreabilidade (R02)

As regras marcadas com `*` são regras autorais criadas pelo grupo para enriquecer o domínio de Estacionamento Inteligente.

| Regra | Descrição | Onde é garantida (Classe / Método) | Exceção Lançada |
| :--- | :--- | :--- | :--- |
| **RN01** | Vagas por tipo: carro, moto, elétrico (com recarga) e PcD. Um veículo só ocupa vaga compatível. | `Vaga.ocupar(Veiculo)` e `Vaga.aceita(Veiculo)` | `VagaIncompativelException` |
| **RN02** | Uma vaga nunca tem dois veículos, e um veículo nunca está em duas vagas ao mesmo tempo. | `Vaga.ocupar(Veiculo)` e `Estacionamento.registrarEntrada(...)` | `VagaOcupadaException`, `VeiculoJaEstacionadoException` |
| **RN03** | Tarifa: 15 min de tolerância, cobrança por hora ou fração, com teto de diária. Mensalistas em dia não pagam. | `TarifaPadrao.calcularValor(...)` e `Estacionamento.pagarTicket(...)` | `IllegalArgumentException` (se tempo < 0) |
| **RN04** | Na vaga de elétrico, a recarga é cobrada à parte, por kWh consumido. | `VagaEletrica.calcularCustoRecarga(double kwh)` e `Ticket.registrarConsumoKwh(...)` | `IllegalArgumentException` (se kWh < 0) |
| **RN05** | Ticket: `ABERTO` → `PAGO` → `ENCERRADO`. A cancela só libera a saída com ticket `PAGO`. | `Ticket.pagar(...)`, `Ticket.encerrar()` e `Estacionamento.liberarCancela(...)` | `TicketNaoPagoException`, `TransicaoEstadoTicketException` |
| **RN06** | A placa deve seguir o padrão Mercosul (`ABC1D23`) ou o antigo (`ABC1234`). | Construtor compacto de `record Placa` | `PlacaInvalidaException` |
| **RN07*** | Vaga bloqueada por manutenção não recebe veículos, e uma vaga ocupada não pode ser bloqueada. | `Vaga.ocupar(Veiculo)` e `Vaga.bloquear()` | `VagaBloqueadaException`, `VagaOcupadaException` |
| **RN08*** | O mensalista só fica isento se a mensalidade estiver em dia. Caso contrário, paga a tarifa normal. | `Mensalista.temIsencao()` e `Estacionamento.pagarTicket(...)` | *(Aplica cobrança normal sem isenção)* |
| **RN09*** | Cada estacionamento é cadastrado com nome e quantidade de vagas por tipo. Antes do menu principal, é preciso selecionar um estacionamento cadastrado, e toda operação acontece nele. | Construtor de `Estacionamento` e `GerenciadorEstacionamentos.selecionar(...)` | `EstacionamentoInvalidoException` |