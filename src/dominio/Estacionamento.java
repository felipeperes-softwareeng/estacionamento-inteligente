package dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Unidade de estacionamento: coordena vagas, tickets, mensalistas, cobrança e saída.
 * RN01-RN05, RN07-RN08. PoliticaDeTarifa é injetada pelo construtor (R06).
 */
public final class Estacionamento {
    private final String nome;
    private final List<Vaga> vagas;
    private final List<Ticket> tickets = new ArrayList<>();
    private final List<Mensalista> mensalistas = new ArrayList<>();
    private final ControleDeOcupacao controleDeOcupacao;
    private final Clock relogio;
    private PoliticaDeTarifa politicaDeTarifa;
    private int proximoNumeroTicket = 1;

    // Unidade isolada: mantém seu próprio controle de placas presentes.
    public Estacionamento(String nome, List<Vaga> vagas, PoliticaDeTarifa politicaDeTarifa) {
        this(nome, vagas, politicaDeTarifa, new ControleDeOcupacao(), Clock.systemDefaultZone());
    }

    // Unidades de um mesmo cadastro compartilham controle e não duplicam placas.
    public Estacionamento(String nome, List<Vaga> vagas, PoliticaDeTarifa politicaDeTarifa,
                          ControleDeOcupacao controleDeOcupacao) {
        this(nome, vagas, politicaDeTarifa, controleDeOcupacao, Clock.systemDefaultZone());
    }

    // Clock injetável: permite testar tempos sem esperar horas reais.
    public Estacionamento(String nome, List<Vaga> vagas, PoliticaDeTarifa politicaDeTarifa,
                          ControleDeOcupacao controleDeOcupacao, Clock relogio) {
        if (nome == null || nome.isBlank()) {
            throw new EstacionamentoInvalidoException("O nome do estacionamento é obrigatório.");
        }
        if (vagas == null || vagas.isEmpty()) {
            throw new EstacionamentoInvalidoException("Ao menos uma vaga é obrigatória.");
        }
        if (politicaDeTarifa == null || controleDeOcupacao == null || relogio == null) {
            throw new EstacionamentoInvalidoException("Política e controle de ocupação são obrigatórios.");
        }
        Set<String> codigos = new HashSet<>();
        for (Vaga vaga : vagas) {
            if (vaga == null) throw new EstacionamentoInvalidoException("Vaga nula não é permitida.");
            if (!codigos.add(vaga.getCodigo())) {
                throw new EstacionamentoInvalidoException("Código de vaga duplicado: " + vaga.getCodigo());
            }
            if (vaga.getStatus() != StatusVaga.LIVRE) {
                throw new EstacionamentoInvalidoException("A vaga deve iniciar livre: " + vaga.getCodigo());
            }
        }
        this.nome = nome.trim();
        this.vagas = new ArrayList<>(vagas);
        this.politicaDeTarifa = politicaDeTarifa;
        this.controleDeOcupacao = controleDeOcupacao;
        this.relogio = relogio;
    }

    // RN08 - Benefício de mensalista vinculado à placa desta unidade.
    public void cadastrarMensalista(Mensalista mensalista) {
        if (mensalista == null) throw new IllegalArgumentException("Mensalista obrigatório.");
        if (buscarMensalistaOuNulo(mensalista.getPlaca()) != null) {
            throw new IllegalArgumentException("Mensalista já cadastrado: " + mensalista.getPlaca().valor());
        }
        mensalistas.add(mensalista);
    }
    public Mensalista localizarMensalista(Placa placa) {
        if (placa == null) throw new IllegalArgumentException("Placa obrigatória.");
        Mensalista m = buscarMensalistaOuNulo(placa);
        if (m == null) throw new IllegalArgumentException("Mensalista não encontrado: " + placa.valor());
        return m;
    }
    public void registrarAtrasoMensalista(Placa placa) { localizarMensalista(placa).registrarAtraso(); }
    public void regularizarMensalidade(Placa placa, double valor) {
        localizarMensalista(placa).regularizarPagamento(valor);
    }
    private Mensalista buscarMensalistaOuNulo(Placa placa) {
        for (Mensalista m : mensalistas) if (m.getPlaca().equals(placa)) return m;
        return null;
    }

    // R06 e desafio C: Strategy trocável enquanto o programa roda.
    public double calcularTarifa(long minutos) {
        if (minutos < 0) throw new IllegalArgumentException("Permanência não pode ser negativa.");
        double valor = politicaDeTarifa.calcularValor(minutos);
        if (!Double.isFinite(valor) || valor < 0) {
            throw new IllegalStateException("Política retornou valor inválido.");
        }
        return arredondarMoeda(valor);
    }
    public void alterarPoliticaDeTarifa(PoliticaDeTarifa novaPolitica) {
        if (novaPolitica == null) throw new EstacionamentoInvalidoException("Política obrigatória.");
        politicaDeTarifa = novaPolitica;
    }
    public String getDescricaoPoliticaDeTarifa() { return politicaDeTarifa.getDescricao(); }

    public Ticket registrarEntrada(Veiculo veiculo, String codigoVaga) {
        if (veiculo == null) throw new IllegalArgumentException("Veículo obrigatório.");
        Vaga vaga = localizarVaga(codigoVaga);
        Placa placa = veiculo.getPlaca();
        // Registro compartilhado impede a mesma placa em duas unidades.
        controleDeOcupacao.registrarEntrada(placa);
        boolean ocupadaAgora = false;
        try {
            vaga.ocupar(veiculo);
            ocupadaAgora = true;
            Ticket ticket = new Ticket(String.format("T%03d", proximoNumeroTicket), veiculo, vaga, LocalDateTime.now(relogio));
            tickets.add(ticket);
            proximoNumeroTicket++;
            return ticket;
        } catch (RuntimeException erro) {
            if (ocupadaAgora) vaga.liberar();
            controleDeOcupacao.desfazerEntrada(placa);
            throw erro;
        }
    }

    public double calcularValorTicket(String idTicket) { return calcularValorTicket(idTicket, 0.0); }
    public double calcularValorTicket(String idTicket, double consumoKwh) {
        Ticket ticket = localizarTicket(idTicket);
        validarTicketAberto(ticket);
        return calcularCobranca(ticket, consumoKwh).total();
    }

    public double pagarTicket(String idTicket, double valorRecebido) {
        return pagarTicket(idTicket, valorRecebido, 0.0);
    }
    public double pagarTicket(String idTicket, double valorRecebido, double consumoKwh) {
        Ticket ticket = localizarTicket(idTicket);
        validarTicketAberto(ticket);
        validarDinheiro(valorRecebido);
        Cobranca cobranca = calcularCobranca(ticket, consumoKwh);
        if (BigDecimal.valueOf(valorRecebido).compareTo(BigDecimal.valueOf(cobranca.total())) != 0) {
            throw new IllegalArgumentException(String.format(Locale.forLanguageTag("pt-BR"),
                    "Pagamento recusado para %s. Devido: R$ %.2f; recebido: R$ %.2f.",
                    idTicket, cobranca.total(), valorRecebido));
        }
        ticket.pagar(cobranca.permanencia(), cobranca.recarga(),
                cobranca.kwh(), cobranca.isento(), cobranca.politica());
        return cobranca.total();
    }

    private Cobranca calcularCobranca(Ticket ticket, double consumoKwh) {
        if (!Double.isFinite(consumoKwh) || consumoKwh < 0) {
            throw new IllegalArgumentException("Consumo em kWh deve ser não negativo e finito.");
        }
        double recarga = 0;
        if (ticket.getVaga() instanceof VagaCarroEletrico eletrica) {
            recarga = eletrica.calcularCustoRecarga(consumoKwh);
        } else if (consumoKwh > 0) {
            throw new IllegalArgumentException("A vaga não possui carregador elétrico.");
        }
        Mensalista mensalista = buscarMensalistaOuNulo(ticket.getVeiculo().getPlaca());
        boolean isento = mensalista != null && mensalista.temIsencao();
        double permanencia = isento ? 0 : calcularTarifa(calcularMinutosDePermanencia(ticket));
        BigDecimal total = BigDecimal.valueOf(permanencia).add(BigDecimal.valueOf(recarga));
        if (total.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("Total da cobrança excede o limite.");
        }
        return new Cobranca(permanencia, recarga, consumoKwh,
                isento, getDescricaoPoliticaDeTarifa(), total.doubleValue());
    }
    private record Cobranca(double permanencia, double recarga, double kwh,
                            boolean isento, String politica, double total) { }

    public void liberarCancela(String idTicket) {
        Ticket ticket = localizarTicket(idTicket);
        if (ticket.getStatus() == StatusTicket.ABERTO) throw new TicketNaoPagoException(idTicket);
        if (ticket.getStatus() != StatusTicket.PAGO) {
            throw new TransicaoEstadoTicketException(idTicket, ticket.getStatus(), "liberar a cancela para");
        }
        Vaga vaga = ticket.getVaga();
        if (vaga.getStatus() != StatusVaga.OCUPADA || vaga.getVeiculoAtual() == null ||
                !vaga.getVeiculoAtual().getPlaca().equals(ticket.getVeiculo().getPlaca())) {
            throw new IllegalStateException("Ocupação incompatível com o ticket " + idTicket);
        }
        controleDeOcupacao.validarSaida(ticket.getVeiculo().getPlaca());
        ticket.encerrar();
        vaga.liberar();
        controleDeOcupacao.registrarSaida(ticket.getVeiculo().getPlaca());
    }

    private long calcularMinutosDePermanencia(Ticket ticket) {
        Duration duracao = Duration.between(ticket.getEntrada(), LocalDateTime.now(relogio));
        if (duracao.isNegative()) throw new IllegalStateException("Relógio anterior à entrada do ticket.");
        long minutos = duracao.toMinutes();
        return duracao.minusMinutes(minutos).isZero() ? minutos : Math.addExact(minutos, 1);
    }
    private void validarTicketAberto(Ticket ticket) {
        if (ticket.getStatus() != StatusTicket.ABERTO) {
            throw new TransicaoEstadoTicketException(ticket.getId(), ticket.getStatus(), "pagar");
        }
    }
    private static void validarDinheiro(double valor) {
        if (!Double.isFinite(valor) || valor < 0 || BigDecimal.valueOf(valor).scale() > 2) {
            throw new IllegalArgumentException("Pagamento não negativo com até duas casas decimais.");
        }
    }
    private static double arredondarMoeda(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public Vaga localizarVaga(String codigo) {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("Código de vaga obrigatório.");
        for (Vaga vaga : vagas) if (vaga.getCodigo().equalsIgnoreCase(codigo.trim())) return vaga;
        throw new IllegalArgumentException("Vaga não encontrada: " + codigo);
    }
    public Ticket localizarTicket(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Ticket obrigatório.");
        for (Ticket ticket : tickets) if (ticket.getId().equalsIgnoreCase(id.trim())) return ticket;
        throw new IllegalArgumentException("Ticket não encontrado: " + id);
    }
    public void bloquearVaga(String codigo) { localizarVaga(codigo).bloquear(); }
    public void desbloquearVaga(String codigo) { localizarVaga(codigo).desbloquear(); }

    // R05: uso polimórfico das duas implementações sem parentesco de herança.
    public List<Cobravel> getCobrancas() {
        List<Cobravel> cobraveis = new ArrayList<>();
        cobraveis.addAll(tickets);
        cobraveis.addAll(mensalistas);
        return List.copyOf(cobraveis);
    }
    public List<String> listarCobrancas() {
        List<String> linhas = new ArrayList<>();
        for (Cobravel cobravel : getCobrancas()) {
            linhas.add(String.format(Locale.forLanguageTag("pt-BR"),
                "%s | valor registrado/contratado: R$ %.2f",
                cobravel.getDescricaoCobranca(), cobravel.getValorCobrado()));
        }
        return List.copyOf(linhas);
    }
    public String getNome() { return nome; }
    public List<Vaga> getVagas() { return List.copyOf(vagas); }
    public List<Ticket> getTickets() { return List.copyOf(tickets); }
    public List<Mensalista> getMensalistas() { return List.copyOf(mensalistas); }
}
