# Glossário — linguagem ubíqua (R01)

| Termo do negócio | Definição acordada | Localização no código |
|---|---|---|
| Estacionamento | Unidade que administra vagas, tickets e mensalistas | `Estacionamento` |
| Cadastro de estacionamentos | Conjunto de unidades disponíveis para operação | `CadastroEstacionamentos` |
| Vaga | Espaço físico com tipo e estado | `Vaga` (abstrata) |
| Vaga elétrica | Espaço para veículo elétrico que pode registrar custo de recarga | `VagaCarroEletrico` |
| Veículo | Automóvel ou moto identificado por placa e tipo | `Veiculo` |
| Placa | Identificador único de veículo, no formato Mercosul ou antigo | `Placa` (`record`) |
| Ocupação | Estado em que uma vaga está associada a um veículo | `Vaga.ocupar()` / `StatusVaga.OCUPADA` |
| Controle de ocupação | Verificação compartilhada da presença de uma placa entre unidades | `ControleDeOcupacao` |
| Bloqueio para manutenção | Impedimento de ocupação de uma vaga livre | `Vaga.bloquear()` / `VagaBloqueadaException` |
| Ticket | Registro de entrada, cobrança e encerramento de estadia | `Ticket` |
| Cancela | Barreira liberada após confirmação do pagamento | `Estacionamento.liberarCancela()` |
| Política de tarifa | Regra substituível que calcula a permanência | `PoliticaDeTarifa` |
| Tarifa padrão | Cobrança por hora ou fração com tolerância e teto | `TarifaPadrao` |
| Tarifa promocional | Desconto percentual aplicado à tarifa padrão | `TarifaPromocional` |
| Mensalista | Cliente vinculado à placa e a uma mensalidade | `Mensalista` |
| Isenção | Dispensa da tarifa de permanência por mensalidade em dia | `Mensalista.temIsencao()` |
| Recarga elétrica | Consumo de energia cobrado por kWh em vaga elétrica | `VagaCarroEletrico.calcularCustoRecarga()` |
| Cobrança | Valor nominal associado a ticket ou mensalidade | `Cobravel` |
| Pagamento | Confirmação de cobrança do ticket após cálculo e validação | `Estacionamento.pagarTicket()` |
| Saída | Encerramento do ticket e liberação de vaga | `Estacionamento.liberarCancela()` |

Não usamos nomes genéricos como `Manager`, `Helper` e `Data` para entidades do domínio.
