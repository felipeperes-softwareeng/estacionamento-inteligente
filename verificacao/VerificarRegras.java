package verificacao;

import aplicacao.CadastroEstacionamentos;
import aplicacao.RelatoriosGerenciais;
import app.MenuConsole;
import dominio.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

/** Verificações independentes, em Java puro: NÃO são o desafio JUnit 5. */
public final class VerificarRegras {
    private static int verificacoes;

    public static void main(String[] args) {
        tarifas();
        vagasEPlacas();
        cobrancaMensalistasERecarga();
        unidadesEPolimorfismo();
        menuNaoEncerraComEntradaInvalida();
        System.out.println("SUCESSO: " + verificacoes + " verificações passaram.");
    }

    private static void tarifas() {
        TarifaPadrao t = new TarifaPadrao(10, 50);
        eq(t.calcularValor(0), 0, "zero minutos");
        eq(t.calcularValor(15), 0, "15 minutos gratuitos");
        eq(t.calcularValor(16), 10, "16 minutos por hora ou fração");
        eq(t.calcularValor(60), 10, "60 minutos");
        eq(t.calcularValor(61), 20, "61 minutos");
        eq(t.calcularValor(1440), 50, "teto de diária");
        eq(t.calcularValor(1441), 60, "nova fração de 24h");
        eq(new TarifaPromocional(10, 50, 20).calcularValor(61), 16, "desconto Strategy");
        erro(IllegalArgumentException.class, () -> t.calcularValor(-1), "tempo negativo");
        erro(IllegalArgumentException.class, () -> new TarifaPadrao(-1, 50), "tarifa negativa");
    }

    private static void vagasEPlacas() {
        new Placa("ABC1234");
        new Placa("ABC1D23");
        erro(PlacaInvalidaException.class, () -> new Placa("123ABCD"), "placa inválida");
        Vaga moto = new VagaMoto("M001");
        erro(VagaIncompativelException.class,
                () -> novaUnidade(moto).registrarEntrada(new Veiculo(new Placa("AAA1234"), TipoVeiculo.CARRO), "M001"),
                "carro incompatível com vaga de moto");
        Vaga bloqueada = new VagaCarro("C010");
        Estacionamento unidade = novaUnidade(bloqueada);
        bloqueada.bloquear();
        erro(VagaBloqueadaException.class, () -> unidade.registrarEntrada(
                new Veiculo(new Placa("AAA1234"), TipoVeiculo.CARRO), "C010"),
                "vaga bloqueada recusa entrada");
        erro(EstacionamentoInvalidoException.class,
                () -> novaUnidade(bloqueada), "vaga bloqueada não nasce cadastrada em outra unidade");
    }

    private static Estacionamento novaUnidade(Vaga vaga) {
        return new Estacionamento("Teste", List.of(vaga), new TarifaPadrao(10, 50));
    }

    private static void cobrancaMensalistasERecarga() {
        RelogioAjustavel relogio = new RelogioAjustavel();
        ControleDeOcupacao controle = new ControleDeOcupacao();
        Estacionamento e = new Estacionamento("Centro", List.of(
                new VagaCarro("C001"), new VagaCarroEletrico("E001", 3)),
                new TarifaPadrao(10, 50), controle, relogio);
        e.cadastrarMensalista(new Mensalista("Ana", new Placa("ABC1D23"), 180, true));
        Ticket eletrico = e.registrarEntrada(
                new Veiculo(new Placa("ABC1D23"), TipoVeiculo.ELETRICO), "E001");
        relogio.avancarMinutos(61);
        eq(e.calcularValorTicket(eletrico.getId(), 2.5), 7.50, "isento paga somente recarga");
        erro(IllegalArgumentException.class,
                () -> e.pagarTicket(eletrico.getId(), 0, 2.5), "não aceita pagamento inferior");
        igual(eletrico.getStatus(), StatusTicket.ABERTO, "pagamento rejeitado preserva status");
        eq(e.pagarTicket(eletrico.getId(), 7.50, 2.5), 7.50, "pagamento aceito");
        igual(eletrico.getStatus(), StatusTicket.PAGO, "transição para pago");
        erro(TransicaoEstadoTicketException.class,
                () -> e.pagarTicket(eletrico.getId(), 7.50, 2.5), "duplicação de pagamento");
        e.liberarCancela(eletrico.getId());
        igual(eletrico.getStatus(), StatusTicket.ENCERRADO, "cancela encerra");
        igual(eletrico.getVaga().getStatus(), StatusVaga.LIVRE, "saída libera vaga");
        erro(TransicaoEstadoTicketException.class,
                () -> e.liberarCancela(eletrico.getId()), "saída duplicada");

        e.cadastrarMensalista(new Mensalista("Beto", new Placa("DEF1234"), 180, false));
        Ticket comum = e.registrarEntrada(
                new Veiculo(new Placa("DEF1234"), TipoVeiculo.CARRO), "C001");
        relogio.avancarMinutos(61);
        eq(e.calcularValorTicket(comum.getId()), 20, "mensalista atrasado paga normal");
        e.regularizarMensalidade(new Placa("DEF1234"), 180);
        eq(e.calcularValorTicket(comum.getId()), 0, "regularizado fica isento");
        erro(TicketNaoPagoException.class,
                () -> e.liberarCancela(comum.getId()), "cancela exige pagamento mesmo se isento");
        eq(e.pagarTicket(comum.getId(), 0), 0, "mensalista isento confirma saída gratuita");
        e.liberarCancela(comum.getId());
        igual(e.getTickets().size(), 2, "histórico preservado");
        erro(UnsupportedOperationException.class,
                () -> e.getTickets().clear(), "lista imutável");
    }

    private static void unidadesEPolimorfismo() {
        CadastroEstacionamentos cadastro = new CadastroEstacionamentos();
        Estacionamento a = cadastro.cadastrar("Centro", 2, 1, 1, 1, 3, new TarifaPadrao(10, 50));
        Estacionamento b = cadastro.cadastrar("Shopping", 2, 0, 0, 0, 3, new TarifaPadrao(10, 50));
        erro(EstacionamentoInvalidoException.class,
                () -> cadastro.cadastrar("cEnTrO", 1, 0, 0, 0, 3, new TarifaPadrao(10, 50)),
                "nome de unidade único");
        Ticket ticket = a.registrarEntrada(new Veiculo(new Placa("ZZZ1234"), TipoVeiculo.CARRO), "C001");
        erro(VeiculoJaEstacionadoException.class,
                () -> b.registrarEntrada(new Veiculo(new Placa("ZZZ1234"), TipoVeiculo.CARRO), "C001"),
                "mesma placa bloqueada em unidades diferentes");
        erro(VagaOcupadaException.class,
                () -> a.bloquearVaga("C001"), "vaga ocupada não bloqueia");
        a.pagarTicket(ticket.getId(), 0);
        a.liberarCancela(ticket.getId());
        b.registrarEntrada(new Veiculo(new Placa("ZZZ1234"), TipoVeiculo.CARRO), "C001");
        a.cadastrarMensalista(new Mensalista("Ana", new Placa("AAA1B23"), 200, true));
        boolean existeTicket = a.getCobrancas().stream().anyMatch(c -> c instanceof Ticket);
        boolean existeMensalista = a.getCobrancas().stream().anyMatch(c -> c instanceof Mensalista);
        verdadeiro(existeTicket && existeMensalista, "List<Cobravel> com tipos independentes");
        cadastro.selecionar("CENTRO");
        RelatoriosGerenciais r = new RelatoriosGerenciais(cadastro);
        verdadeiro(!r.ticketsPorSituacao().isEmpty(), "relatório de tickets");
        verdadeiro(!r.receitaPorTipo().isEmpty(), "relatório de receitas");
        verdadeiro(r.ocupacaoPorTipo().isEmpty(), "relatório vazio sem ocupação");
        igual(r.listarVagasOrdenadas(false).get(0).getCodigo(), "C001", "Comparable");
        verdadeiro(r.listarVagasOrdenadas(true).size() == a.getVagas().size(), "Comparator");
        a.alterarPoliticaDeTarifa(new TarifaPromocional(10, 50, 20));
        eq(a.calcularTarifa(61), 16, "troca de estratégia executável");
    }

    private static void menuNaoEncerraComEntradaInvalida() {
        CadastroEstacionamentos cadastro = new CadastroEstacionamentos();
        cadastro.cadastrar("Centro", 1, 0, 0, 0, 3, new TarifaPadrao(10, 50));
        String comandos = "2\nCentro\n1\nPLACAERRADA\n12\n0\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream saida = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            new MenuConsole(cadastro,
                new ByteArrayInputStream(comandos.getBytes(StandardCharsets.UTF_8)), saida).executar();
        }
        String texto = bytes.toString(StandardCharsets.UTF_8);
        verdadeiro(texto.contains("OPERAÇÃO RECUSADA"), "menu captura exceção do domínio");
        verdadeiro(texto.contains("Até logo!"), "menu segue até sair após erro");
    }

    private static final class RelogioAjustavel extends Clock {
        private Instant agora = Instant.parse("2026-11-19T10:00:00Z");
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zona) { return Clock.fixed(agora, zona); }
        @Override public Instant instant() { return agora; }
        public void avancarMinutos(long minutos) { agora = agora.plusSeconds(minutos * 60); }
    }

    private static void verdadeiro(boolean condicao, String descricao) {
        verificacoes++;
        if (!condicao) throw new AssertionError("Falha: " + descricao);
    }
    private static void igual(Object atual, Object esperado, String descricao) {
        verdadeiro(atual.equals(esperado), descricao + " (obtido " + atual + "; esperado " + esperado + ")");
    }
    private static void eq(double atual, double esperado, String descricao) {
        verdadeiro(Math.abs(atual - esperado) < 0.00001, descricao);
    }
    private static void erro(Class<? extends Throwable> classe, Runnable acao, String descricao) {
        try { acao.run(); }
        catch (Throwable falha) {
            verdadeiro(classe.isInstance(falha), descricao + " (erro: " + falha + ")");
            return;
        }
        throw new AssertionError("Deveria lançar " + classe.getSimpleName() + ": " + descricao);
    }
}
