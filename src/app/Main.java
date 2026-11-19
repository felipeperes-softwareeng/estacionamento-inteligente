
package app;

import aplicacao.CadastroEstacionamentos;
import dominio.*;

public final class Main {

    public static void main(String[] args) {

        CadastroEstacionamentos cadastro =
                new CadastroEstacionamentos();

        // ==========================================
        // 1. CADASTRO DO ESTACIONAMENTO
        // ==========================================

        Estacionamento centro = cadastro.cadastrar(
                "Estacionamento do Jessé",
                5,     // Vagas para carros
                3,     // Vagas para motos
                2,     // Vagas para elétricos
                2,     // Vagas PCD
                3.00,  // Preço por kWh
                new TarifaPadrao(10.00, 50.00)
        );

        // ==========================================
        // 2. CADASTRO DOS MENSALISTAS
        // ==========================================

        // Ana: mensalidade em dia.
        centro.cadastrarMensalista(
                new Mensalista(
                        "Ana Souza",
                        new Placa("ELT1A23"),
                        180.00,
                        true
                )
        );

        // João: mensalidade atrasada.
        centro.cadastrarMensalista(
                new Mensalista(
                        "Joao Silva",
                        new Placa("JKL9876"),
                        150.00,
                        false
                )
        );

        centro.cadastrarMensalista(
                new Mensalista(
                        "Jessé",
                        new Placa("DCQ8C27"),
                        150.00,
                        true
                )
        );

        // ==========================================
        // 3. REGISTRO DOS VEÍCULOS
        // ==========================================

        // T001 - Carro comum: ABERTO
        centro.registrarEntrada(
                new Veiculo(
                        new Placa("ABC1234"),
                        TipoVeiculo.CARRO
                ),
                "C001"
        );

        // T002 - Moto: ABERTO
        centro.registrarEntrada(
                new Veiculo(
                        new Placa("DEF5678"),
                        TipoVeiculo.MOTO
                ),
                "M001"
        );

        // T003 - Elétrico mensalista: será PAGO
        Ticket ticketEletrico = centro.registrarEntrada(
                new Veiculo(
                        new Placa("ELT1A23"),
                        TipoVeiculo.ELETRICO
                ),
                "E001"
        );

        // Ana é mensalista em dia.
        // Paga somente a recarga de 2 kWh = R$ 6,00.
        double valorEletrico = centro.calcularValorTicket(
                ticketEletrico.getId(), 2.0
        );

        centro.pagarTicket(
                ticketEletrico.getId(),
                valorEletrico,
                2.0
        );

        // Não libera a cancela.
        // T003 fica PAGO e E001 continua OCUPADA.

        // T004 - Veículo PCD: ABERTO
        centro.registrarEntrada(
                new Veiculo(
                        new Placa("GHI2B34"),
                        TipoVeiculo.PCD
                ),
                "P001"
        );

        // T005 - Mensalista atrasado: ABERTO
        centro.registrarEntrada(
                new Veiculo(
                        new Placa("JKL9876"),
                        TipoVeiculo.CARRO
                ),
                "C002"
        );

        // T006 - Elétrico comum: será ENCERRADO
        Ticket ticketEncerrado = centro.registrarEntrada(
                new Veiculo(
                        new Placa("PQR4D56"),
                        TipoVeiculo.ELETRICO
                ),
                "E002"
        );

        // Recarga de 4 kWh = R$ 12,00.
        double valorEncerrado = centro.calcularValorTicket(
                ticketEncerrado.getId(), 4.0
        );

        centro.pagarTicket(
                ticketEncerrado.getId(),
                valorEncerrado,
                4.0
        );

        // Libera a saída.
        // T006 fica ENCERRADO e E002 volta a LIVRE.
        centro.liberarCancela(ticketEncerrado.getId());

        // ==========================================
        // 4. INICIALIZAÇÃO DO MENU
        // ==========================================

        System.out.println(
                "Dados de demonstracao carregados!"
        );

        System.out.println(
                "1 estacionamento, 2 mensalistas e 6 tickets."
        );

        new MenuConsole(cadastro).executar();
    }
}
