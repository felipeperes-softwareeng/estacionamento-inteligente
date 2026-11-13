classDiagram
    direction TB

    class Placa {
        <<record>>
        +String valor
    }

    class TipoVeiculo {
        <<enumeration>>
        CARRO
        MOTO
        ELETRICO
        PCD
    }

    class StatusVaga {
        <<enumeration>>
        LIVRE
        OCUPADA
        BLOQUEADA
    }

    class StatusTicket {
        <<enumeration>>
        ABERTO
        PAGO
        ENCERRADO
    }

    class Veiculo {
        -Placa placa
        -TipoVeiculo tipo
        +getPlaca() Placa
        +getTipo() TipoVeiculo
    }

    class Vaga {
        <<abstract>>
        -String codigo
        -StatusVaga status
        -Veiculo veiculoAtual
        +aceita(Veiculo veiculo)* boolean
        +ocupar(Veiculo veiculo) void
        +liberar() void
        +bloquear() void
        +desbloquear() void
        +compareTo(Vaga outra) int
    }

    class VagaCarro {
        +aceita(Veiculo veiculo) boolean
    }
    class VagaMoto {
        +aceita(Veiculo veiculo) boolean
    }
    class VagaEletrica {
        -double precoPorKwh
        +aceita(Veiculo veiculo) boolean
        +calcularCustoRecarga(double kwh) double
    }
    class VagaPcd {
        +aceita(Veiculo veiculo) boolean
    }

    class PoliticaDeTarifa {
        <<interface>>
        +calcularValor(long minutos) double
        +getDescricao() String
    }

    class TarifaPadrao {
        -double valorHora
        -double tetoDiaria
        +calcularValor(long minutos) double
    }

    class TarifaPromocional {
        -double valorHora
        -double tetoDiaria
        +calcularValor(long minutos) double
    }

    class Cobravel {
        <<interface>>
        +getValorCobrado() double
        +getDescricaoCobranca() String
    }

    class Mensalista {
        -String nome
        -Placa placa
        - double valorMensalidade
        -boolean emDia
        +temIsencao() boolean
        +regularizarPagamento() void
        +registrarAtraso() void
        +getValorCobrado() double
    }

    class Ticket {
        -String id
        -Veiculo veiculo
        -Vaga vaga
        -StatusTicket status
        -double valorPago
        +pagar(double valor) void
        +encerrar() void
        +getValorCobrado() double
        +compareTo(Ticket outro) int
    }

    class Estacionamento {
        -String nome
        -List~Vaga~ vagas
        -List~Ticket~ tickets
        -List~Mensalista~ mensalistas
        -PoliticaDeTarifa politicaDeTarifa
        +registrarEntrada(Veiculo v, String codVaga) Ticket
        +calcularEPagarTicket(String idTicket, long minutos, double kwh) double
        +liberarCancela(String idTicket) void
    }

    Veiculo *-- Placa
    Veiculo --> TipoVeiculo
    Vaga --> StatusVaga
    Vaga o-- Veiculo
    Vaga <|-- VagaCarro
    Vaga <|-- VagaMoto
    Vaga <|-- VagaEletrica
    Vaga <|-- VagaPcd
    PoliticaDeTarifa <|.. TarifaPadrao
    PoliticaDeTarifa <|.. TarifaPromocional
    Cobravel <|.. Ticket
    Cobravel <|.. Mensalista
    Mensalista *-- Placa
    Ticket --> StatusTicket
    Ticket --> Veiculo
    Ticket --> Vaga
    Estacionamento *-- Vaga
    Estacionamento *-- Ticket
    Estacionamento *-- Mensalista
    Estacionamento o-- PoliticaDeTarifa