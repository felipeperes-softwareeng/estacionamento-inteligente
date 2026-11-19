package app;

import aplicacao.CadastroEstacionamentos;
import aplicacao.OperacoesEstacionamento;
import aplicacao.RelatoriosGerenciais;
import dominio.*;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** R10/R12 - Única camada que lê teclado e imprime resultados. */
public final class MenuConsole {
    private final Scanner entrada;
    private final PrintStream saida;
    private final CadastroEstacionamentos cadastro;
    private final OperacoesEstacionamento operacoes;
    private final RelatoriosGerenciais relatorios;

    public MenuConsole(CadastroEstacionamentos cadastro) {
        this(cadastro, System.in, System.out);
    }
    public MenuConsole(CadastroEstacionamentos cadastro, InputStream teclado, PrintStream saida) {
        if (cadastro == null || teclado == null || saida == null) {
            throw new IllegalArgumentException("Cadastro, teclado e saída são obrigatórios.");
        }
        this.cadastro = cadastro;
        this.operacoes = new OperacoesEstacionamento(cadastro);
        this.relatorios = new RelatoriosGerenciais(cadastro);
        this.entrada = new Scanner(teclado, StandardCharsets.UTF_8);
        this.saida = saida;
    }

    public void executar() {
        saida.println("=== ESTACIONAMENTO INTELIGENTE ===");
        saida.println("Sistema educacional em Java 21 | dados em memória");
        boolean ativo = true;
        while (ativo) {
            try {
                if (cadastro.temSelecao()) {
                    menuDaUnidade();
                } else {
                    menuInicial();
                }
                int opcao = lerInteiro("Opção: ");
                if (cadastro.temSelecao()) {
                    executarOpcaoUnidade(opcao);
                } else {
                    if (opcao == 0) { ativo = false; }
                    else executarOpcaoInicial(opcao);
                }
            } catch (PlacaInvalidaException | VeiculoJaEstacionadoException |
                     VagaOcupadaException | VagaBloqueadaException |
                     VagaIncompativelException | TicketNaoPagoException |
                     TransicaoEstadoTicketException | EstacionamentoInvalidoException erro) {
                // R10: exceções nomeadas de domínio, tratadas sem derrubar o programa.
                saida.println("OPERAÇÃO RECUSADA: " + erro.getMessage());
            } catch (IllegalArgumentException | IllegalStateException erro) {
                saida.println("ENTRADA OU OPERAÇÃO INVÁLIDA: " + erro.getMessage());
            } catch (NoSuchElementException erro) {
                saida.println("Entrada encerrada. Até logo.");
                return;
            }
        }
        saida.println("Até logo!");
    }

    private void menuInicial() {
        saida.println("\n--- UNIDADES ---");
        saida.println("1 - Cadastrar estacionamento");
        saida.println("2 - Selecionar estacionamento");
        saida.println("3 - Listar estacionamentos");
        saida.println("0 - Sair");
    }
    private void executarOpcaoInicial(int opcao) {
        switch (opcao) {
            case 1 -> cadastrarEstacionamento();
            case 2 -> selecionarEstacionamento();
            case 3 -> listarEstacionamentos();
            default -> saida.println("Opção inválida.");
        }
    }
    private void menuDaUnidade() {
        saida.println("\n--- " + cadastro.unidadeSelecionada().getNome().toUpperCase(Locale.ROOT) + " ---");
        saida.println("1 - Registrar entrada");
        saida.println("2 - Consultar valor do ticket");
        saida.println("3 - Registrar pagamento");
        saida.println("4 - Liberar cancela / registrar saída");
        saida.println("5 - Bloquear ou desbloquear vaga");
        saida.println("6 - Gerenciar mensalistas");
        saida.println("7 - Listar vagas (Comparable ou Comparator)");
        saida.println("8 - Listar tickets");
        saida.println("9 - Relatórios gerenciais (Streams)");
        saida.println("10 - Trocar política de tarifa (Strategy)");
        saida.println("11 - Listar cobranças (List<Cobravel>)");
        saida.println("12 - Voltar à seleção de estacionamentos");
        saida.println("0 - Sair da unidade / voltar à seleção");
    }
    private void executarOpcaoUnidade(int opcao) {
        switch (opcao) {
            case 1 -> registrarEntrada();
            case 2 -> consultarValor();
            case 3 -> pagarTicket();
            case 4 -> liberarCancela();
            case 5 -> manutencao();
            case 6 -> mensalistas();
            case 7 -> listarVagas();
            case 8 -> listarTickets();
            case 9 -> relatorios();
            case 10 -> trocarTarifa();
            case 11 -> mostrarLinhas(operacoes.cobrancas());
            case 0, 12 -> cadastro.limparSelecao();
            default -> saida.println("Opção inválida.");
        }
    }

    private void cadastrarEstacionamento() {
        String nome = lerTexto("Nome: ");
        int carros = lerInteiro("Quantidade de vagas CARRO: ");
        int motos = lerInteiro("Quantidade de vagas MOTO: ");
        int eletricos = lerInteiro("Quantidade de vagas ELETRICO: ");
        int pcd = lerInteiro("Quantidade de vagas PCD: ");
        double precoKwh = lerDecimal("Preço por kWh (ex.: 3,00): ");
        double hora = lerDecimal("Preço da hora: ");
        double diaria = lerDecimal("Teto por 24h: ");
        cadastro.cadastrar(nome, carros, motos, eletricos, pcd, precoKwh,
                new TarifaPadrao(hora, diaria));
        saida.println("Estacionamento cadastrado. Selecione-o pelo nome para operar.");
    }
    private void selecionarEstacionamento() {
        listarEstacionamentos();
        String nome = lerTexto("Digite o nome exato (maiúsculas/minúsculas são ignoradas): ");
        cadastro.selecionar(nome);
        saida.println("Selecionado: " + cadastro.unidadeSelecionada().getNome());
    }
    private void listarEstacionamentos() {
        List<Estacionamento> unidades = cadastro.listar();
        if (unidades.isEmpty()) { saida.println("Nenhuma unidade cadastrada."); return; }
        for (Estacionamento e : unidades) {
            long ocupadas = e.getVagas().stream().filter(v -> v.getStatus() == StatusVaga.OCUPADA).count();
            saida.printf(Locale.forLanguageTag("pt-BR"), "%s | %d vagas | %d ocupadas | %s%n",
                    e.getNome(), e.getVagas().size(), ocupadas, e.getDescricaoPoliticaDeTarifa());
        }
    }
    private void registrarEntrada() {
        Placa placa = lerPlaca("Placa (ABC1234 ou ABC1D23): ");
        TipoVeiculo tipo = lerTipoVeiculo();
        String codigo = lerTexto("Código da vaga (ex.: C001, M001, E001, P001): ");
        Ticket ticket = operacoes.entrada(new Veiculo(placa, tipo), codigo);
        saida.println("Entrada registrada! Ticket: " + ticket.getId() +
                " | placa: " + ticket.getVeiculo().getPlaca().valor() +
                " | entrada: " + ticket.getEntrada());
    }
    private void consultarValor() {
        String id = lerTexto("Ticket: ");
        double consumo = lerDecimal("Consumo em kWh (0 se não recarregou): ");
        dinheiro("Valor devido", operacoes.consultarCobranca(id, consumo));
    }
    private void pagarTicket() {
        String id = lerTexto("Ticket: ");
        double consumo = lerDecimal("Consumo final em kWh (0 se não recarregou): ");
        dinheiro("Valor devido agora", operacoes.consultarCobranca(id, consumo));
        double recebido = lerDecimal("Valor recebido (exato, sem troco): ");
        double valor = operacoes.receberPagamento(id, recebido, consumo);
        dinheiro("Pagamento registrado", valor);
        saida.println("Ticket PAGO. Utilize a opção 4 para liberar a cancela.");
    }
    private void liberarCancela() {
        String id = lerTexto("Ticket: ");
        operacoes.liberarCancela(id);
        saida.println("Cancela liberada, ticket ENCERRADO e vaga LIVRE.");
    }
    private void manutencao() {
        String codigo = lerTexto("Código da vaga: ");
        int acao = lerInteiro("1 bloquear / 2 desbloquear: ");
        if (acao == 1) operacoes.bloquear(codigo);
        else if (acao == 2) operacoes.desbloquear(codigo);
        else { saida.println("Ação inválida."); return; }
        saida.println("Situação da vaga atualizada.");
    }
    private void mensalistas() {
        saida.println("1 cadastrar | 2 registrar atraso | 3 regularizar | 4 listar");
        int opcao = lerInteiro("Opção: ");
        switch (opcao) {
            case 1 -> {
                String nome = lerTexto("Nome: ");
                Placa placa = lerPlaca("Placa: ");
                double valor = lerDecimal("Mensalidade: ");
                int situacao = lerInteiro("1 em dia / 2 atrasado: ");
                if (situacao != 1 && situacao != 2) throw new IllegalArgumentException("Situação inválida.");
                operacoes.cadastrarMensalista(new Mensalista(nome, placa, valor, situacao == 1));
                saida.println("Mensalista cadastrado.");
            }
            case 2 -> {
                operacoes.registrarAtraso(lerPlaca("Placa do mensalista: "));
                saida.println("Atraso registrado; permanência não será isenta.");
            }
            case 3 -> {
                Placa placa = lerPlaca("Placa do mensalista: ");
                double valor = lerDecimal("Pagamento integral recebido: ");
                operacoes.regularizarMensalidade(placa, valor);
                saida.println("Mensalidade regularizada; isenção recuperada.");
            }
            case 4 -> {
                if (operacoes.mensalistas().isEmpty()) saida.println("Nenhum mensalista.");
                else operacoes.mensalistas().forEach(m -> saida.printf(
                    "%s | %s | %s | R$ %.2f%n", m.getNome(), m.getPlaca().valor(),
                    m.temIsencao() ? "EM DIA" : "ATRASADO", m.getValorMensalidade()));
            }
            default -> saida.println("Opção inválida.");
        }
    }
    private void listarVagas() {
        int ordem = lerInteiro("1 por código (Comparable) / 2 por status (Comparator): ");
        if (ordem != 1 && ordem != 2) throw new IllegalArgumentException("Ordem inválida.");
        for (Vaga vaga : relatorios.listarVagasOrdenadas(ordem == 2)) {
            String placa = vaga.getVeiculoAtual() == null ? "-" : vaga.getVeiculoAtual().getPlaca().valor();
            saida.printf("%s | %-8s | %-8s | placa: %s%n", vaga.getCodigo(),
                    RelatoriosGerenciais.categoria(vaga), vaga.getStatus(), placa);
        }
    }
    private void listarTickets() {
        if (operacoes.tickets().isEmpty()) { saida.println("Nenhum ticket."); return; }
        for (Ticket ticket : relatorios.listarTicketsPorEntrada()) {
            saida.printf(Locale.forLanguageTag("pt-BR"),
                    "%s | placa %s | vaga %s | %s | R$ %.2f | %s%n",
                    ticket.getId(), ticket.getVeiculo().getPlaca().valor(),
                    ticket.getVaga().getCodigo(), ticket.getStatus(),
                    ticket.getValorPago(), ticket.getEntrada());
        }
    }
    private void relatorios() {
        saida.println("1 Ocupação por tipo | 2 Receita por tipo | 3 Tickets por estado | 4 Todos");
        int opcao = lerInteiro("Opção: ");
        switch (opcao) {
            case 1 -> mostrarRelatorio("Ocupação por tipo", relatorios.ocupacaoPorTipo());
            case 2 -> mostrarRelatorio("Receita de tickets por tipo", relatorios.receitaPorTipo());
            case 3 -> mostrarRelatorio("Tickets por situação", relatorios.ticketsPorSituacao());
            case 4 -> {
                mostrarRelatorio("Ocupação por tipo", relatorios.ocupacaoPorTipo());
                mostrarRelatorio("Receita de tickets por tipo", relatorios.receitaPorTipo());
                mostrarRelatorio("Tickets por situação", relatorios.ticketsPorSituacao());
            }
            default -> saida.println("Opção inválida.");
        }
    }
    private void trocarTarifa() {
        saida.println("Política atual: " + operacoes.selecionado().getDescricaoPoliticaDeTarifa());
        int opcao = lerInteiro("1 padrão | 2 promocional: ");
        if (opcao != 1 && opcao != 2) throw new IllegalArgumentException("Política inválida.");
        double hora = lerDecimal("Valor da hora: ");
        double teto = lerDecimal("Teto por 24 horas: ");
        PoliticaDeTarifa politica = opcao == 1 ? new TarifaPadrao(hora, teto)
                : new TarifaPromocional(hora, teto, lerDecimal("Percentual de desconto: "));
        operacoes.trocarTarifa(politica);
        saida.println("Nova política: " + operacoes.selecionado().getDescricaoPoliticaDeTarifa());
    }
    private void mostrarRelatorio(String nome, List<String> linhas) {
        saida.println("\n--- " + nome + " ---");
        mostrarLinhas(linhas);
    }
    private void mostrarLinhas(List<String> linhas) {
        if (linhas.isEmpty()) saida.println("Nenhum registro para este relatório.");
        else linhas.forEach(saida::println);
    }
    private void dinheiro(String legenda, double valor) {
        saida.printf(Locale.forLanguageTag("pt-BR"), "%s: R$ %.2f%n", legenda, valor);
    }
    private String lerTexto(String titulo) {
        saida.print(titulo);
        if (!entrada.hasNextLine()) throw new NoSuchElementException("Fim da entrada");
        String texto = entrada.nextLine().trim();
        if (texto.isEmpty()) throw new IllegalArgumentException("O campo não pode ficar vazio.");
        return texto;
    }
    private int lerInteiro(String titulo) {
        try { return Integer.parseInt(lerTexto(titulo)); }
        catch (NumberFormatException erro) { throw new IllegalArgumentException("Informe um número inteiro válido."); }
    }
    private double lerDecimal(String titulo) {
        String texto = lerTexto(titulo).replace(',', '.');
        try {
            double valor = Double.parseDouble(texto);
            if (!Double.isFinite(valor)) throw new NumberFormatException();
            return valor;
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException("Informe um valor decimal válido (ex.: 10,50).");
        }
    }
    private Placa lerPlaca(String titulo) {
        return new Placa(lerTexto(titulo).toUpperCase(Locale.ROOT));
    }
    private TipoVeiculo lerTipoVeiculo() {
        int opcao = lerInteiro("Tipo: 1 CARRO | 2 MOTO | 3 ELETRICO | 4 PCD: ");
        return switch (opcao) {
            case 1 -> TipoVeiculo.CARRO;
            case 2 -> TipoVeiculo.MOTO;
            case 3 -> TipoVeiculo.ELETRICO;
            case 4 -> TipoVeiculo.PCD;
            default -> throw new IllegalArgumentException("Tipo de veículo inválido.");
        };
    }
}
