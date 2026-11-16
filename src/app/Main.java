
package app;

import dominio.*;

public class Main {

    public static void main(String[] args) {

        // Criar veículo
        Placa placa = new Placa("ABC1D23");
        Veiculo carro = new Veiculo(
                placa,
                TipoVeiculo.CARRO
        );

        // Criar e ocupar uma vaga
        Vaga vaga = new VagaCarro("A01");
        vaga.ocupar(carro);

        // Criar ticket
        Ticket ticket = new Ticket("T001", carro, vaga);

        System.out.println("===== TICKET CRIADO =====");
        System.out.println("ID: " + ticket.getId());
        System.out.println("Status: " + ticket.getStatus());

        // TESTE 1 - Encerrar sem pagamento
        System.out.println("\n===== TESTE 1 =====");

        try {
            ticket.encerrar();
        } catch (IllegalStateException e) {
            System.out.println(
                    "Operação recusada: " + e.getMessage()
            );
        }

        // TESTE 2 - Realizar pagamento
        System.out.println("\n===== TESTE 2 =====");

        ticket.pagar(20.00);

        System.out.println("Pagamento realizado!");
        System.out.println("Valor pago: R$ " + ticket.getValorPago());
        System.out.println("Status: " + ticket.getStatus());

        // TESTE 3 - Encerrar ticket pago
        System.out.println("\n===== TESTE 3 =====");

        ticket.encerrar();

        System.out.println("Ticket encerrado com sucesso!");
        System.out.println("Status: " + ticket.getStatus());

        // TESTE 4 - Tentar encerrar novamente
        System.out.println("\n===== TESTE 4 =====");

        try {
            ticket.encerrar();
        } catch (IllegalStateException e) {
            System.out.println(
                    "Operação recusada: " + e.getMessage()
            );
        }

        // Resultado final
        System.out.println("\n===== RESULTADO FINAL =====");
        System.out.println("Ticket: " + ticket.getId());
        System.out.println("Status: " + ticket.getStatus());
        System.out.println("Valor pago: R$ " + ticket.getValorPago());
    }
}
