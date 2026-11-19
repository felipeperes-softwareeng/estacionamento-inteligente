
package dominio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Estacionamento {

    private final String nome;
    private final List<Vaga> vagas;
    private final List<Ticket> tickets;

    // R06 - Composição por interface
    private PoliticaDeTarifa politicaDeTarifa;

    private int proximoNumeroTicket;

    // Construtor
    public Estacionamento(
            String nome,
            List<Vaga> vagas,
            PoliticaDeTarifa politicaDeTarifa
    ) {

        if (nome == null || nome.isBlank()) {
            throw new EstacionamentoInvalidoException(
                    "O nome do estacionamento é obrigatório."
            );
        }

        if (vagas == null || vagas.isEmpty()) {
            throw new EstacionamentoInvalidoException(
                    "O estacionamento deve possuir pelo menos uma vaga."
            );
        }

        if (politicaDeTarifa == null) {
            throw new EstacionamentoInvalidoException(
                    "A política de tarifa é obrigatória."
            );
        }

        Set<String> codigos = new HashSet<>();

        for (Vaga vaga : vagas) {

            if (vaga == null) {
                throw new EstacionamentoInvalidoException(
                        "Não é permitido cadastrar uma vaga nula."
                );
            }

            if (!codigos.add(vaga.getCodigo())) {
                throw new EstacionamentoInvalidoException(
                        "Código de vaga duplicado: "
                                + vaga.getCodigo()
                );
            }

            if (vaga.getStatus() != StatusVaga.LIVRE) {
                throw new EstacionamentoInvalidoException(
                        "A vaga " + vaga.getCodigo()
                                + " deve estar livre no cadastro."
                );
            }
        }

        this.nome = nome;
        this.vagas = new ArrayList<>(vagas);
        this.tickets = new ArrayList<>();

        // A política é recebida por meio do construtor
        this.politicaDeTarifa = politicaDeTarifa;

        this.proximoNumeroTicket = 1;
    }

    // RN03 - Calcula a tarifa usando a política ativa
    public double calcularTarifa(long minutos) {

        double valor = politicaDeTarifa.calcularValor(minutos);

        // Protege o estacionamento contra uma política inválida
        if (!Double.isFinite(valor) || valor < 0) {
            throw new IllegalStateException(
                    "A política de tarifa retornou um valor inválido."
            );
        }

        return valor;
    }

    // Desafio C - Permite trocar a estratégia em execução
    public void alterarPoliticaDeTarifa(
            PoliticaDeTarifa novaPolitica
    ) {

        if (novaPolitica == null) {
            throw new EstacionamentoInvalidoException(
                    "A nova política de tarifa não pode ser nula."
            );
        }

        this.politicaDeTarifa = novaPolitica;
    }

    // Consulta qual política de tarifa está ativa
    public String getDescricaoPoliticaDeTarifa() {
        return politicaDeTarifa.getDescricao();
    }

    // RN02 - Registrar entrada de veículo
    public Ticket registrarEntrada(
            Veiculo veiculo,
            String codigoVaga
    ) {

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

        // Impede que a mesma placa ocupe duas vagas
        for (Vaga vaga : vagas) {

            if (vaga.getStatus() == StatusVaga.OCUPADA
                    && vaga.getVeiculoAtual() != null
                    && vaga.getVeiculoAtual().getPlaca()
                    .equals(veiculo.getPlaca())) {

                throw new VeiculoJaEstacionadoException(
                        veiculo.getPlaca().valor()
                );
            }
        }

        // Localiza a vaga
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

        // Gera o ticket
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

    // RN05 - Libera cancela somente após pagamento
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

        // Verifica o pagamento
        if (ticketEncontrado.getStatus() == StatusTicket.ABERTO) {
            throw new TicketNaoPagoException(idTicket);
        }

        // Impede encerramento duplicado
        if (ticketEncontrado.getStatus() != StatusTicket.PAGO) {
            throw new TransicaoEstadoTicketException(
                    idTicket,
                    ticketEncontrado.getStatus(),
                    "liberar a cancela para"
            );
        }

        // Confere a ocupação correspondente
        Vaga vaga = ticketEncontrado.getVaga();

        if (vaga.getStatus() != StatusVaga.OCUPADA
                || vaga.getVeiculoAtual() == null
                || !vaga.getVeiculoAtual().getPlaca()
                .equals(ticketEncontrado.getVeiculo().getPlaca())) {

            throw new IllegalStateException(
                    "A ocupação da vaga não corresponde ao ticket "
                            + idTicket + "."
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
