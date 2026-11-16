
package app;

import dominio.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Criar as vagas do estacionamento
        Vaga vaga1 = new VagaCarro("A01");
        Vaga vaga2 = new VagaCarro("A02");
        Vaga vaga3 = new VagaMoto("M01");

        // Criar estacionamento
        Estacionamento estacionamento = new Estacionamento(
                "Estacionamento Centro",
                List.of(vaga1, vaga2, vaga3)
        );

        System.out.println("===== ESTACIONAMENTO =====");
        System.out.println("Nome: " + estacionamento.getNome());
        System.out.println("Total de vagas: "
                + estacionamento.getVagas().size());

        // Criar os veículos
        Veiculo carro1 = new Veiculo(
                new Placa("ABC1D23"),
                TipoVeiculo.CARRO
        );

        Veiculo carro2 = new Veiculo(
                new Placa("DEF2G34"),
                TipoVeiculo.CARRO
        );

        // TESTE 1 - Entrada válida
        System.out.println("\n===== TESTE 1: ENTRADA =====");

        Ticket ticket = estacionamento.registrarEntrada(
                carro1, "A01"
        );

        System.out.println("Entrada registrada!");
        System.out.println("Ticket: " + ticket.getId());
        System.out.println("Placa: "
                + ticket.getVeiculo().getPlaca().valor());
        System.out.println("Vaga: " + ticket.getVaga().getCodigo());
        System.out.println("Status: " + ticket.getStatus());

        // TESTE 2 - Mesmo veículo em outra vaga
        System.out.println("\n===== TESTE 2: PLACA DUPLICADA =====");

        try {
            estacionamento.registrarEntrada(carro1, "A02");

        } catch (IllegalStateException e) {
            System.out.println("Operação recusada: "
                    + e.getMessage());
        }

        // TESTE 3 - Veículo incompatível
        System.out.println("\n===== TESTE 3: INCOMPATIBILIDADE =====");

        try {
            estacionamento.registrarEntrada(carro2, "M01");

        } catch (IllegalArgumentException e) {
            System.out.println("Operação recusada: "
                    + e.getMessage());
        }

        // TESTE 4 - Vaga ocupada
        System.out.println("\n===== TESTE 4: VAGA OCUPADA =====");

        try {
            estacionamento.registrarEntrada(carro2, "A01");

        } catch (IllegalStateException e) {
            System.out.println("Operação recusada: "
                    + e.getMessage());
        }

        // TESTE 5 - Vaga bloqueada
        System.out.println("\n===== TESTE 5: VAGA BLOQUEADA =====");

        vaga2.bloquear();

        try {
            estacionamento.registrarEntrada(carro2, "A02");

        } catch (IllegalStateException e) {
            System.out.println("Operação recusada: "
                    + e.getMessage());
        }

        // Exibir a situação final
        System.out.println("\n===== RESULTADO FINAL =====");

        System.out.println("Tickets gerados: "
                + estacionamento.getTickets().size());

        for (Vaga vaga : estacionamento.getVagas()) {
            System.out.println(
                    "Vaga " + vaga.getCodigo()
                            + " - " + vaga.getStatus()
            );
        }
    }
}
