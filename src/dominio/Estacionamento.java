package dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Unidade de estacionamento. Coordena entrada, mensalistas, cobrança e saída.
 * RN02, RN03, RN04, RN05 e RN08. A política é injetada pelo construtor (R06).
 */
public class Estacionamento {

    private final String nome;
    private final List<Vaga> vagas;
    private final List<Ticket> tickets;
    private final List<Mensalista> mensalistas;
    private PoliticaDeTarifa politicaDeTarifa;
    private int proximoNumeroTicket;

    public Estacionamento(String nome, List<Vaga> vagas,
                          PoliticaDeTarifa politicaDeTarifa) {
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
                        "Não é permitido cadastrar vaga nula."
                );
            }
            if (!codigos.add(vaga.getCodigo())) {
                throw new EstacionamentoInvalidoException(
                        "Código de vaga duplicado: " + vaga.getCodigo()
                );
            }
            if (vaga.getStatus() != StatusVaga.LIVRE) {
                throw new EstacionamentoInvalidoException(
                        "A vaga " + vaga.getCodigo() + " deve estar livre no cadastro."
                );
            }
        }

        this.nome = nome.trim();
        this.vagas = new ArrayList<>(vagas);
        this.tickets = new ArrayList<>();
        this.mensalistas = new ArrayList<>();
        this.politicaDeTarifa = politicaDeTarifa;
        this.proximoNumeroTicket = 1;
    }

    // Etapa 18: mensalistas e RN08.
    public void cadastrarMensalista(Mensalista mensalista) {
        if (mensalista == null) {
            throw new IllegalArgumentException("O mensalista é obrigatório.");
        }
        if (buscarMensalistaOuNulo(mensalista.getPlaca()) != null) {
            throw new IllegalArgumentException(
                    "Já existe mensalista cadastrado para a placa "
                            + mensalista.getPlaca().valor() + "."
            );
        }
        mensalistas.add(mensalista);
    }

    public Mensalista localizarMensalista(Placa placa) {
        if (placa == null) {
            throw new IllegalArgumentException("A placa é obrigatória.");
        }
        Mensalista mensalista = buscarMensalistaOuNulo(placa);
        if (mensalista == null) {
            throw new IllegalArgumentException(
                    "Mensalista não encontrado para a placa " + placa.valor() + "."
            );
        }
        return mensalista;
    }

    public void registrarAtrasoMensalista(Placa placa) {
        localizarMensalista(placa).registrarAtraso();
    }

    public void regularizarMensalidade(Placa placa, double valorRecebido) {
        localizarMensalista(placa).regularizarPagamento(valorRecebido);
    }

    private Mensalista buscarMensalistaOuNulo(Placa placa) {
        for (Mensalista mensalista : mensalistas) {
            if (mensalista.getPlaca().equals(placa)) {
                return mensalista;
            }
        }
        return null;
    }

    // R06 / Desafio C: Strategy.
    public double calcularTarifa(long minutos) {
        if (minutos < 0) {
            throw new IllegalArgumentException(
                    "O tempo de permanência não pode ser negativo."
            );
        }
        double valor = politicaDeTarifa.calcularValor(minutos);
        if (!Double.isFinite(valor) || valor < 0) {
            throw new IllegalStateException(
                    "A política de tarifa retornou um valor inválido."
            );
        }
        return arredondarMoeda(valor);
    }

    public void alterarPoliticaDeTarifa(PoliticaDeTarifa novaPolitica) {
        if (novaPolitica == null) {
            throw new EstacionamentoInvalidoException(
                    "A nova política de tarifa não pode ser nula."
            );
        }
        this.politicaDeTarifa = novaPolitica;
    }

    public String getDescricaoPoliticaDeTarifa() {
        return politicaDeTarifa.getDescricao();
    }

    // RN01/RN02: ocupação e emissão de ticket controladas.
    public Ticket registrarEntrada(Veiculo veiculo, String codigoVaga) {
        if (veiculo == null) {
            throw new IllegalArgumentException("O veículo é obrigatório.");
        }
        if (codigoVaga == null || codigoVaga.isBlank()) {
            throw new IllegalArgumentException("O código da vaga é obrigatório.");
        }

        for (Vaga vaga : vagas) {
            if (vaga.getStatus() == StatusVaga.OCUPADA
                    && vaga.getVeiculoAtual() != null
                    && vaga.getVeiculoAtual().getPlaca().equals(veiculo.getPlaca())) {
                throw new VeiculoJaEstacionadoException(
                        veiculo.getPlaca().valor()
                );
            }
        }

        Vaga vagaEscolhida = null;
        for (Vaga vaga : vagas) {
            if (vaga.getCodigo().equals(codigoVaga)) {
                vagaEscolhida = vaga;
                break;
            }
        }
        if (vagaEscolhida == null) {
            throw new IllegalArgumentException("Vaga não encontrada: " + codigoVaga);
        }

        vagaEscolhida.ocupar(veiculo);
        try {
            String idTicket = String.format("T%03d", proximoNumeroTicket);
            Ticket ticket = new Ticket(idTicket, veiculo, vagaEscolhida);
            tickets.add(ticket);
            proximoNumeroTicket++;
            return ticket;
        } catch (RuntimeException erro) {
            // Se a criação falhar, não deixa a vaga ocupada sem ticket.
            vagaEscolhida.liberar();
            throw erro;
        }
    }

    // Etapas 18/19: consulta de cobrança sem alterar o ticket.
    public double calcularValorTicket(String idTicket) {
        return calcularValorTicket(idTicket, 0.0);
    }

    public double calcularValorTicket(String idTicket, double consumoKwh) {
        Ticket ticket = localizarTicket(idTicket);
        validarTicketAberto(ticket);
        return calcularCobranca(ticket, consumoKwh).total();
    }

    // Etapas 17/18/19: confirma somente o valor exato. Sem troco/parcelamento.
    public double pagarTicket(String idTicket, double valorRecebido) {
        return pagarTicket(idTicket, valorRecebido, 0.0);
    }

    public double pagarTicket(String idTicket, double valorRecebido,
                             double consumoKwh) {
        Ticket ticket = localizarTicket(idTicket);
        validarTicketAberto(ticket);
        validarValorRecebido(valorRecebido);

        Cobranca cobranca = calcularCobranca(ticket, consumoKwh);
        if (BigDecimal.valueOf(valorRecebido).compareTo(
                BigDecimal.valueOf(cobranca.total())) != 0) {
            throw new IllegalArgumentException(
                    String.format(Locale.forLanguageTag("pt-BR"),
                            "Pagamento recusado para %s. Devido: R$ %.2f; recebido: R$ %.2f.",
                            idTicket, cobranca.total(), valorRecebido)
            );
        }

        // Somente o estacionamento solicita a transição para PAGO.
        ticket.pagar(cobranca.permanencia(), cobranca.recarga(),
                cobranca.kwh(), cobranca.isento(), cobranca.politica());
        return cobranca.total();
    }

    // Concentra o cálculo para consulta e pagamento utilizarem as mesmas regras.
    private Cobranca calcularCobranca(Ticket ticket, double consumoKwh) {
        if (!Double.isFinite(consumoKwh) || consumoKwh < 0) {
            throw new IllegalArgumentException(
                    "O consumo em kWh deve ser não negativo e finito."
            );
        }

        Vaga vaga = ticket.getVaga();
        double recarga = 0.0;
        if (vaga instanceof VagaCarroEletrico vagaEletrica) {
            recarga = vagaEletrica.calcularCustoRecarga(consumoKwh);
        } else if (consumoKwh > 0) {
            throw new IllegalArgumentException(
                    "A vaga " + vaga.getCodigo() + " não possui recarga elétrica."
            );
        }

        Mensalista mensalista = buscarMensalistaOuNulo(
                ticket.getVeiculo().getPlaca()
        );
        boolean isento = mensalista != null && mensalista.temIsencao();
        long minutos = calcularMinutosDePermanencia(ticket);
        double permanencia = isento ? 0.0 : calcularTarifa(minutos);

        BigDecimal soma = BigDecimal.valueOf(permanencia)
                .add(BigDecimal.valueOf(recarga));
        if (soma.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("Valor da cobrança excede o limite permitido.");
        }

        return new Cobranca(permanencia, recarga, consumoKwh, isento,
                getDescricaoPoliticaDeTarifa(), soma.doubleValue());
    }

    private record Cobranca(double permanencia, double recarga,
                            double kwh, boolean isento,
                            String politica, double total) { }

    // RN05: cancela somente com ticket PAGO.
    public void liberarCancela(String idTicket) {
        Ticket ticket = localizarTicket(idTicket);
        if (ticket.getStatus() == StatusTicket.ABERTO) {
            throw new TicketNaoPagoException(idTicket);
        }
        if (ticket.getStatus() != StatusTicket.PAGO) {
            throw new TransicaoEstadoTicketException(
                    idTicket, ticket.getStatus(), "liberar a cancela para"
            );
        }

        Vaga vaga = ticket.getVaga();
        if (vaga.getStatus() != StatusVaga.OCUPADA
                || vaga.getVeiculoAtual() == null
                || !vaga.getVeiculoAtual().getPlaca()
                        .equals(ticket.getVeiculo().getPlaca())) {
            throw new IllegalStateException(
                    "A ocupação da vaga não corresponde ao ticket " + idTicket + "."
            );
        }

        ticket.encerrar();
        vaga.liberar();
    }

    private long calcularMinutosDePermanencia(Ticket ticket) {
        Duration duracao = Duration.between(
                ticket.getEntrada(), LocalDateTime.now()
        );
        if (duracao.isNegative()) {
            throw new IllegalStateException(
                    "O horário atual é anterior à entrada do ticket " + ticket.getId() + "."
            );
        }
        long minutos = duracao.toMinutes();
        if (!duracao.minusMinutes(minutos).isZero()) {
            minutos++;
        }
        return minutos;
    }

    private void validarTicketAberto(Ticket ticket) {
        if (ticket.getStatus() != StatusTicket.ABERTO) {
            throw new TransicaoEstadoTicketException(
                    ticket.getId(), ticket.getStatus(), "pagar"
            );
        }
    }

    private Ticket localizarTicket(String idTicket) {
        if (idTicket == null || idTicket.isBlank()) {
            throw new IllegalArgumentException(
                    "O identificador do ticket é obrigatório."
            );
        }
        for (Ticket ticket : tickets) {
            if (ticket.getId().equals(idTicket)) {
                return ticket;
            }
        }
        throw new IllegalArgumentException("Ticket não encontrado: " + idTicket);
    }

    private void validarValorRecebido(double valor) {
        if (!Double.isFinite(valor) || valor < 0
                || BigDecimal.valueOf(valor).scale() > 2) {
            throw new IllegalArgumentException(
                    "O valor recebido deve ser não negativo e ter até duas casas decimais."
            );
        }
    }

    private static double arredondarMoeda(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    // Etapa 20: uso real da interface comum com classes sem herança entre si.
    public List<Cobravel> getCobrancas() {
        List<Cobravel> cobraveis = new ArrayList<>();
        cobraveis.addAll(tickets);
        cobraveis.addAll(mensalistas);
        return List.copyOf(cobraveis);
    }

    public List<String> listarCobrancas() {
        List<String> descricoes = new ArrayList<>();
        for (Cobravel cobravel : getCobrancas()) {
            descricoes.add(String.format(Locale.forLanguageTag("pt-BR"),
                    "%s | Valor registrado/contratado: R$ %.2f",
                    cobravel.getDescricaoCobranca(),
                    cobravel.getValorCobrado()));
        }
        return List.copyOf(descricoes);
    }

    public String getNome() { return nome; }
    public List<Vaga> getVagas() { return List.copyOf(vagas); }
    public List<Ticket> getTickets() { return List.copyOf(tickets); }
    public List<Mensalista> getMensalistas() { return List.copyOf(mensalistas); }
}
