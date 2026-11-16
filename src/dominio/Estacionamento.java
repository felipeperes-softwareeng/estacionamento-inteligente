
package dominio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Estacionamento {

    private final String nome;
    private final List<Vaga> vagas;
    private final List<Ticket> tickets;

    private int proximoNumeroTicket;

    // Construtor
    public Estacionamento(String nome, List<Vaga> vagas) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do estacionamento é obrigatório."
            );
        }

        if (vagas == null || vagas.isEmpty()) {
            throw new IllegalArgumentException(
                    "O estacionamento deve possuir pelo menos uma vaga."
            );
        }

        Set<String> codigos = new HashSet<>();

        for (Vaga vaga : vagas) {

            if (vaga == null) {
                throw new IllegalArgumentException(
                        "Não é permitido cadastrar uma vaga nula."
                );
            }

            if (!codigos.add(vaga.getCodigo())) {
                throw new IllegalArgumentException(
                        "Código de vaga duplicado: " + vaga.getCodigo()
                );
            }

            if (vaga.getStatus() != StatusVaga.LIVRE) {
                throw new IllegalArgumentException(
                        "A vaga " + vaga.getCodigo()
                                + " deve estar livre no cadastro."
                );
            }
        }

        this.nome = nome;
        this.vagas = new ArrayList<>(vagas);
        this.tickets = new ArrayList<>();
        this.proximoNumeroTicket = 1;
    }

    // RN02 - Registrar entrada de veículo
    public Ticket registrarEntrada(Veiculo veiculo, String codigoVaga) {

        if (veiculo == null) {
            throw new IllegalArgumentException(
                    "O veículo é obrigatório."
            );
        }

        if (codigoVaga == null || codigoVaga.isBlank()) {
            throw new IllegalArgumentException(
                    "O código da vaga é obrigatório."
            );
        }

        // Verifica se a placa já está estacionada
        for (Vaga vaga : vagas) {

            if (vaga.getStatus() == StatusVaga.OCUPADA
                    && vaga.getVeiculoAtual() != null
                    && vaga.getVeiculoAtual().getPlaca()
                    .equals(veiculo.getPlaca())) {

                throw new IllegalStateException(
                        "O veículo de placa "
                                + veiculo.getPlaca().valor()
                                + " já está estacionado."
                );
            }
        }

        // Localiza a vaga solicitada
        Vaga vagaEscolhida = null;

        for (Vaga vaga : vagas) {
            if (vaga.getCodigo().equals(codigoVaga)) {
                vagaEscolhida = vaga;
                break;
            }
        }

        if (vagaEscolhida == null) {
            throw new IllegalArgumentException(
                    "Vaga não encontrada: " + codigoVaga
            );
        }

        // Ocupa a vaga
        vagaEscolhida.ocupar(veiculo);

        // Gera um ticket
        String idTicket = String.format(
                "T%03d", proximoNumeroTicket
        );

        Ticket ticket = new Ticket(
                idTicket,
                veiculo,
                vagaEscolhida
        );

        tickets.add(ticket);
        proximoNumeroTicket++;

        return ticket;
    }

    // RN05 - Liberar a cancela somente com ticket pago
    public void liberarCancela(String idTicket) {

        if (idTicket == null || idTicket.isBlank()) {
            throw new IllegalArgumentException(
                    "O identificador do ticket é obrigatório."
            );
        }

        // Localiza o ticket
        Ticket ticketEncontrado = null;

        for (Ticket ticket : tickets) {
            if (ticket.getId().equals(idTicket)) {
                ticketEncontrado = ticket;
                break;
            }
        }

        if (ticketEncontrado == null) {
            throw new IllegalArgumentException(
                    "Ticket não encontrado: " + idTicket
            );
        }

        // Verifica se o ticket está pago
        if (ticketEncontrado.getStatus() != StatusTicket.PAGO) {
            throw new IllegalStateException(
                    "A saída não foi autorizada. O ticket "
                            + idTicket + " não está PAGO."
            );
        }

        Vaga vaga = ticketEncontrado.getVaga();

        // Confere se o veículo continua na vaga correta
        if (vaga.getStatus() != StatusVaga.OCUPADA
                || vaga.getVeiculoAtual() == null
                || !vaga.getVeiculoAtual().getPlaca()
                .equals(ticketEncontrado.getVeiculo().getPlaca())) {

            throw new IllegalStateException(
                    "A ocupação da vaga não corresponde ao ticket."
            );
        }

        // Encerra o ticket e libera a vaga
        ticketEncontrado.encerrar();
        vaga.liberar();
    }

    // Métodos de consulta
    public String getNome() {
        return nome;
    }

    public List<Vaga> getVagas() {
        return List.copyOf(vagas);
    }

    public List<Ticket> getTickets() {
        return List.copyOf(tickets);
    }
}
