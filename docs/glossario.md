# Glossário da Linguagem Ubíqua (R01)

Este glossário define os termos do domínio de **Estacionamento Inteligente** e seu mapeamento direto no código-fonte, evitando termos genéricos como `Manager`, `Helper` ou `Data`.

| Termo do negócio | Significado acordado | Elemento no código |
| :--- | :--- | :--- |
| **Estacionamento (Pátio)** | Unidade física que gerencia um conjunto de vagas, mensalistas, tickets e aplica uma política de tarifa. | `class Estacionamento` |
| **Vaga** | Espaço numerado destinado ao estacionamento de um único veículo compatível. Pode estar livre, ocupada ou bloqueada para manutenção. | `abstract class Vaga` (`VagaCarro`, `VagaMoto`, `VagaEletrica`, `VagaPcd`) |
| **Placa** | Identificador oficial do veículo no padrão Mercosul (`ABC1D23`) ou antigo (`ABC1234`). | `record Placa` |
| **Ticket** | Comprovante de entrada de um veículo em uma vaga, que controla o ciclo de vida (`ABERTO`, `PAGO`, `ENCERRADO`) e o valor devido. | `class Ticket`, `enum StatusTicket` |
| **Mensalista** | Cliente recorrente vinculado a uma placa que possui isenção da tarifa de permanência caso sua mensalidade esteja em dia. | `class Mensalista` |
| **Política de Tarifa** | Regra de negócio que calcula o valor do estacionamento considerando 15 minutos de tolerância, valor por hora/fração e teto de diária. | `interface PoliticaDeTarifa` (`TarifaPadrao`, `TarifaPromocional`) |
| **Item Cobrável** | Qualquer elemento ou serviço do domínio que gera um valor financeiro a ser pago (ex.: Ticket de estadia ou Mensalista). | `interface Cobravel` |
| **Recarga Elétrica** | Consumo de energia medido em kWh em uma vaga elétrica, cobrado à parte da tarifa de estadia. | `VagaEletrica.calcularCustoRecarga(double kwh)` |
| **Liberar Cancela** | Ato de encerrar um ticket já pago, liberando a saída do veículo e tornando a vaga livre novamente. | `Estacionamento.liberarCancela(String idTicket)` / `Ticket.encerrar()` |
| **Bloquear Vaga** | Colocar uma vaga livre em estado de manutenção para impedir que receba veículos. | `Vaga.bloquear()` |
| **Vaga Ocupada / Indisponível** | Tentativa de estacionar em vaga já ocupada/bloqueada ou tentativa de bloquear para manutenção uma vaga com veículo. | `VagaOcupadaException`, `VagaBloqueadaException` |
| **Saída sem Pagamento** | Tentativa de abrir a cancela para um veículo cujo ticket ainda não está no estado `PAGO`. | `TicketNaoPagoException` |