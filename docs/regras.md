# Regras de negócio e rastreabilidade (R02)

As regras RN07, RN08 e RN09, assinaladas com `*`, são **autorais** do grupo. As regras RN01 a RN06 vêm do card D3 do enunciado.

| Regra | Invariante de negócio | Classes e métodos que a garantem | Erros relevantes |
|---|---|---|---|
| **RN01** | CARRO, MOTO, ELETRICO e PCD só ocupam vagas compatíveis. | `Vaga.ocupar(Veiculo)` chama `Vaga.aceita()`; implementações `VagaCarro`, `VagaMoto`, `VagaCarroEletrico`, `VagaPcd` | `VagaIncompativelException` |
| **RN02** | Uma vaga tem no máximo um veículo; uma placa não ocupa duas vagas nem duas unidades do mesmo cadastro. | `Vaga.ocupar()`, `ControleDeOcupacao.registrarEntrada()`, `Estacionamento.registrarEntrada()`, `Estacionamento.liberarCancela()` | `VagaOcupadaException`, `VeiculoJaEstacionadoException` |
| **RN03** | Até 15 min são gratuitos; depois hora/fração e teto por diária; mensalista regular não paga permanência. | `TarifaPadrao.calcularValor()`, `TarifaPromocional.calcularValor()`, `Estacionamento.calcularCobranca()` | `IllegalArgumentException` para parâmetros inválidos |
| **RN04** | Recarga em vaga elétrica tem cobrança adicional por kWh. | `VagaCarroEletrico.calcularCustoRecarga()`, `Estacionamento.calcularCobranca()`, `Ticket.pagar()` | `IllegalArgumentException` para kWh negativo ou vaga sem recarga |
| **RN05** | Ticket percorre `ABERTO → PAGO → ENCERRADO`; cancela só libera ticket PAGO. | `Ticket.pagar()`, `Ticket.encerrar()`, `Estacionamento.pagarTicket()`, `Estacionamento.liberarCancela()` | `TicketNaoPagoException`, `TransicaoEstadoTicketException` |
| **RN06** | Placas Mercosul `ABC1D23` e antiga `ABC1234`; outros formatos inválidos. | Construtor compacto de `Placa` (`record`) | `PlacaInvalidaException` |
| **RN07*** | Somente vaga LIVRE pode ser bloqueada; vaga BLOQUEADA não recebe veículos. | `Vaga.bloquear()`, `Vaga.desbloquear()`, `Vaga.ocupar()` | `VagaBloqueadaException`, `VagaOcupadaException` |
| **RN08*** | Mensalista só é isento de permanência se estiver em dia; inadimplente paga tarifa normal; recarga não é isenta. | `Mensalista.temIsencao()`, `Mensalista.registrarAtraso()`, `Mensalista.regularizarPagamento()`, `Estacionamento.calcularCobranca()` | `IllegalArgumentException`, `IllegalStateException` |
| **RN09*** | Cada unidade tem nome, número de vagas por tipo e deve ser selecionada antes das operações. | `CadastroEstacionamentos.cadastrar()`, `.selecionar()`, `.unidadeSelecionada()` e `MenuConsole` | `EstacionamentoInvalidoException` |

## Critérios de demonstração

- Tentar entrar com `CARRO` em `M001`: receber exceção de incompatibilidade.
- Entrar com placa `AAA1234` em `Centro`; tentar a mesma placa em `Shopping`: duplicidade bloqueada.
- Bloquear uma vaga ocupada: recusa; bloquear uma vaga livre e tentar entrada: recusa.
- Tentar liberar cancela de ticket `ABERTO`: `TicketNaoPagoException`.
- Mostrar troca da `TarifaPadrao` para `TarifaPromocional` pela opção 10 do menu.
- Mensalista em dia com veículo elétrico: a permanência é isenta, mas a recarga tem custo.
- Mensalista atrasado paga permanência; regularização recupera isenção.

O sistema também verifica pagamento exato antes da transição para `PAGO`, não permite pagamento duplicado e retorna cópias defensivas das listas de consultas.
